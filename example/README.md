# flutter_tenc_map_example

Demonstrates how to use the flutter_tenc_map plugin.

## Getting Started

This project is a starting point for a Flutter application.

A few resources to get you started if this is your first Flutter project:

- [Lab: Write your first Flutter app](https://flutter.dev/docs/get-started/codelab)
- [Cookbook: Useful Flutter samples](https://flutter.dev/docs/cookbook)

For help getting started with Flutter, view our
[online documentation](https://flutter.dev/docs), which offers tutorials,
samples, guidance on mobile development, and a full API reference.

##android：配置地图Key
在AndroidManifest.xml中配置
    <application
            ...
        <meta-data
            android:name="TencentMapSDK"
            android:value="H5PBZ-PNV3V-F2WPG-UUZCN-O6MPQ-LLB3Q" />
            ...
    </application>

    value为腾讯地图的KEY值

##使用方法
 1.导入包

 import 'package:flutter_tenc_map/flutter_tenc_map.dart';

 使用前初始化

 FlutterTencMap.init("");

 3.请求定位

  Location location = await FlutterTencMap.getLocation(1000);

  参数为多久请求一次，默认10000毫秒

 4.添加定位监听

   FlutterTencMap.locationController.listener.add((location) {
        //work
      });

 5.停止定位

  bool isOK = await FlutterTencMap.stopLocation();