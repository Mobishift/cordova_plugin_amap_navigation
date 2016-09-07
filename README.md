# cordova_plugin_amap_navigation

使用[高德地图](http://lbs.amap.com/api/android-navi-sdk/summary/)sdk进行导航的cordova plugin

## 版本要求

android >= 4.0.0

ios >= 7.0

## 开始

### variable
androidamapkey: 你的高德地图android api key（集成android必须）

iflytekappid: 你的科大讯飞app id（集成android必须）

iosamapkey: 你的高德地图ios api key（集成ios必须）

### 例子

```shell
cordova plugin add https://github.com/Mobishift/cordova_plugin_amap_navigation --variable androidamapkey=1234 --variable iflytekappid=5678 --variable iosamapkey=7890
```

## 使用

调用：

```js
var successCallback = function(message){
  //do something  
};

var errorCallback = function(message){
    console.log(message);  
};

cordova.plugins.AMapNavigation.navigation({
   lng: 起始地的经度,
   lat: 起始地的纬度
}, {
    lng: 终点的经度,
    lat: 终点的纬度
}, successCallback, errorCallback);

```

注意：调用时需要保证apk包签名与在开放平台的设置相同，否则无法进行导航
