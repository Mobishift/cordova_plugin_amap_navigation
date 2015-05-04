var exec = require('cordova/exec');

exports.coolMethod = function(arg0, success, error) {
    exec(success, error, "AMapNavigation", "coolMethod", [arg0]);
};

exports.navigation = function(startPoint, endPoint, successCallback, errorCallback){
    successCallback = successCallback || function(){};
    errorCallback = errorCallback || function(){};
    var isNumber = function(point){
        return typeof point.lng === 'number' && typeof point.lat === 'number'; 
    };
    if(!isNumber(startPoint)){
        errorCallback('start point invalid');

    }else if(!isNumber(endPoint)){
        errorCallback('end point invaild');
    }else{
        exports.exec('navigation', [startPoint.lng, startPoint.lat, endPoint.lng, endPoint.lat], successCallback, errorCallback);
    }
};
