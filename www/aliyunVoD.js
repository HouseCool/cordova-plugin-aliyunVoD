
var exec = require('cordova/exec');

exports.VODUploadClient = function() {
    exec(function(){}, function(){}, "aliyunVoD", "VoDUploadClient", []);
}