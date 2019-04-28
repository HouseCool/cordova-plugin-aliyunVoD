
var exec = require('cordova/exec');

//初始化aliyunVOD
exports.init = function(accessKeyId, accessKeySecret, secretToken){
    exec(function(){}, function(){}, "aliyunVoD", "init", [accessKeyId, accessKeySecret, secretToken]);
}

//添加文件
exports.addFile = function(success, error,filePath) {
    return exec(success, error, "aliyunVoD", "addFile", [filePath]);
}

//开始上传
exports.start = function(success, error) {
    return exec(success, error, "aliyunVoD", "start", []);
}

//上传进度
exports.onUploadProgress = function(success, error) {
    return exec(success, error, "aliyunVoD", "onUploadProgress", []);
}