# cordova_plugin_amap_navigation

使用高德地图sdk进行导航的cordova plugin

## 开始

### Android

将plugin.xml中的YOU_API_KEY修改为你申请的高德地图Android Sdk Api key

### IOS

在项目的config.xml中加入：

<preference name="amapapikey" value="你的高德地图IOS api key" />

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