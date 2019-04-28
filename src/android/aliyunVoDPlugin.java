package com.plugin.aliyunVoD;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.vod.upload.ResumableVODUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODUploadClient;
import com.alibaba.sdk.android.vod.upload.VODUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.UploadFileInfo;
import com.alibaba.sdk.android.vod.upload.model.VodInfo;
import com.alibaba.sdk.android.vod.upload.model.VodUploadResult;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import static org.apache.cordova.CordovaWebViewImpl.TAG;

public class aliyunVoDPlugin extends CordovaPlugin {

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 2;
    private String expireTime = "3600";
    protected JSONArray args;
    protected CallbackContext callbackContext;
    protected CallbackContext uploadcallback;
    protected CallbackContext progresscallback;
    VODUploadClient uploader;
    private int index = 0;
    public static String VOD_REGION = "cn-shanghai";
    public static boolean VOD_RECORD_UPLOAD_PROGRESS_ENABLED = true;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        this.args = args;
        if (action.equals("init")){
            String accessKeyId = this.args.getString( 0 );
            String accessKeySecret = this.args.getString( 1 );
            String secretToken = this.args.getString( 2 );
            this.init(accessKeyId,accessKeySecret,secretToken);
            return true;
        }else if (action.equals("addFile")){
            this.callbackContext = callbackContext;
            sendNoResultPluginResult(this.callbackContext);
            this.addFile();
            return true;
        }else if(action.equals("start")){
            this.uploadcallback = callbackContext;
            sendNoResultPluginResult(uploadcallback);
            this.start();
            return true;
        }else if(action.equals("onUploadProgress")){
            this.progresscallback = callbackContext;
            sendNoResultPluginResult(progresscallback);
            return true;
        }
        return false;
    }


    //Vod初始化配置
    private void init(String accessKeyId,String accessKeySecret,String secretToken){
        index = 0;
        uploader = new VODUploadClientImpl(this.cordova.getContext());
        uploader.setRegion(VOD_REGION);
        uploader.setRecordUploadProgressEnabled(VOD_RECORD_UPLOAD_PROGRESS_ENABLED);
        ResumableVODUploadCallback callback = new ResumableVODUploadCallback() {

            /**
             上传完成回调
             @param info 上传文件信息
             @param results 上传结果信息
             */
            @Override
            public void onUploadFinished(UploadFileInfo info, VodUploadResult results) {
                super.onUploadFinished(info, results);
                JSONObject jo = new JSONObject(  );
                try {
                    jo.put( "FilePath",info.getFilePath() );
                    jo.put( "Url",results.getImageUrl() );
                    jo.put( "Videoid",results.getVideoid() );
                    jo.put( "Status", info.getStatus());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pushData(uploadcallback,jo);
            }


            /**
             上传失败回调
             @param info 上传文件信息
             @param code 错误码
             @param message 错误描述
             */
            @Override
            public void onUploadFailed(UploadFileInfo info, String code, String message) {
                JSONObject jo = new JSONObject(  );
                try {
                    jo.put( "FilePath",info.getFilePath() );
                    jo.put( "code",code );
                    jo.put( "message",message );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, jo);
                result.setKeepCallback(true);
                uploadcallback.sendPluginResult(result);
            }

            /**
             上传进度回调
             @param info 上传文件信息
             @param uploadedSize 已上传大小
             @param totalSize 总大小
             */
            @Override
            public void onUploadProgress(UploadFileInfo info, long uploadedSize, long totalSize) {
                Log.d( TAG, "onUploadProgress:  " + info.getFilePath() + " " + uploadedSize + " " + totalSize);
                JSONObject jb = new JSONObject(  );
                try {
                    jb.put( "filepath",info.getFilePath() );
                    jb.put( "uploadedSize",uploadedSize );
                    jb.put( "totalSize",totalSize );
                    jb.put( "Bucket",info.getBucket() );
                    jb.put( "Status",info.getStatus() );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PluginResult result = new PluginResult(PluginResult.Status.OK, jb);
                result.setKeepCallback(true);
                progresscallback.sendPluginResult(result);
            }

            /**
             上传开始重试回调
             */
            @Override
            public void onUploadRetry(String code, String message) {
                OSSLog.logError("onUploadRetry ------------- ");
            }
        };

        uploader.init(accessKeyId, accessKeySecret, secretToken, expireTime, callback);
        uploader.setPartSize(1024 * 1024);
    }

    //添加文件
    private void addFile() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission( this.cordova.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.cordova.getActivity(), PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
        String filepath = "";
        try {
            filepath = this.args.getString( 0 );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d( TAG, "addFile: "+filepath );

        File file = new File(filepath);
        Log.d( TAG, "addFile: "+file.exists() );
        if(!file.exists()){
            Toast.makeText(this.cordova.getContext(),"文件路径错误", Toast.LENGTH_SHORT).show();
            return;
        }
        VodInfo vodInfo = new VodInfo();
        String[] filesp = filepath.split( "\\/" );
        String filename = filesp[filesp.length-1];
        vodInfo.setTitle( filename );
        uploader.addFile(filepath,vodInfo);

        // 获取刚添加的文件
        UploadFileInfo uploadFileInfo = uploader.listFiles().get(uploader.listFiles().size() - 1);
        Log.d( TAG, (index+1)+"  addFile: "+uploader.listFiles().size() );
        if(uploader.listFiles().size() == index+1){
            JSONObject ja = new JSONObject(  );
            try {
                ja.put( "Title",filename );
                ja.put( "filepath",uploadFileInfo.getFilePath());
                ja.put( "index",uploader.listFiles().size() - 1 );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.pushData(this.callbackContext,ja);
        }else {
            this.pushError(this.callbackContext,"File upload failed"  );
        }
        index++;
    }

    //开始上传
    private void start(){
        List<UploadFileInfo> listFile =  uploader.listFiles();
        if(listFile.size() == 0){
            Toast.makeText(this.cordova.getContext(),"请添加文件", Toast.LENGTH_SHORT).show();
            return;
        }
        uploader.start();
    }

    private void pushData(CallbackContext callbackContext,final JSONObject msg) {
        PluginResult result = new PluginResult(PluginResult.Status.OK, msg);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    private void sendNoResultPluginResult(CallbackContext callbackContext) {
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    public static void pushError(CallbackContext callbackContext,final String msg) {
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, msg);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }
}