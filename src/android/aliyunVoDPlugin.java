package com.plugin.aliyunVoD;

import android.app.Activity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
 
public class aliyunVoDPlugin extends CordovaPlugin {
 
    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();

    }
 
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        final Activity activity = this.cordova.getActivity();
        if (action.equals("showInfo")){
 
        }else if (action.equals("VoDUploadClient")){
            return true;
        }
        return false;
    }
}