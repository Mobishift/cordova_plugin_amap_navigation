# cordova_plugin_amap_navigation

使用高德地图sdk进行导航的cordova plugin

## 开始

```shell
cordova plugin add https://github.com/Mobishift/cordova_plugin_amap_navigation --variable amapapikey=你的高德地图APIKEY --variable iflytekappid=你的科大讯飞语音合成appid
```

```xml
<preference name="amapapikey" value="你的高德地图IOS api key" />
<preference name="iflytekappid" value="你的科大讯飞语音合成app id" />
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
