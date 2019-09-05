# flutter_tenc_map
腾讯地图定位

[![pub package]](https://pub.dartlang.org/packages/image_cropper)
# android：配置地图Key
在AndroidManifest.xml中配置
````xml
    <application
            ...
        <meta-data
            android:name="TencentMapSDK"
            android:value="H5PBZ-PNV3V-F2WPG-UUZCN-O6MPQ-LLB3Q" />
            ...
    </application>
````
    value为腾讯地图的KEY值

# 使用方法
 1.导入包

 import 'package:flutter_tenc_map/flutter_tenc_map.dart';

 使用前初始化

 FlutterTencMap.init("");

 3.请求定位
````dart
  Location location = await FlutterTencMap.getLocation(1000);
````
  参数为多久请求一次，默认10000毫秒

 4.添加定位监听
````dart
   FlutterTencMap.locationController.listener.add((location) {
        //work
      });
````
 5.停止定位
````dart
  bool isOK = await FlutterTencMap.stopLocation();
````
