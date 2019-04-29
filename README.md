# aliyun-VOD 
> This plugin is Ali cloud video on-demand file upload function.

## How to Install

Cordova:
```bash
cordova plugin add https://github.com/HouseCool/cordova-plugin-aliyunVoD.git
```

### Android
> Out of the box

#### init

```
aliyunVoD.init(accessKeyId,accessKeySecret,secretToken);
```

#### addFile

```
aliyunVoD.addFile(success,error,"storage/emulated/0/DCIM/Camera/083a4ccdf74ef9aa5ab9beb9a8a0abda.mp4")
```

#### start

```
aliyunVoD.start(success,err);
```

#### onUploadProgress

```
aliyunVoD.onUploadProgress(success,err);
```



