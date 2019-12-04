import 'dart:async';
import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

import 'model/location.dart';
import 'controll/location_controller.dart';

export 'model/location.dart';
export 'controll/location_controller.dart';
export 'package:flutter_tenc_map/map_view/map_view.dart';
export 'package:flutter_tenc_map/model/my_location_style.dart';
export 'package:flutter_tenc_map/model/tag_tip.dart';

class FlutterTencMap {
  static const MethodChannel tencChannel =
      const MethodChannel('flutter_tenc_map/channel');

  static LocationController locationController = LocationController();

  ///初始化
  static Future<String> init(String key) async {
    debugPrint("key只对ios有效,android的key请在mAndroidManifest.xml里设置");

    ///监听native端返回的数据
    tencChannel.setMethodCallHandler((methodCall) async {
      switch (methodCall.method) {
        case "flutter_tenc_map_backLocation":
          //返回定位信息
          Location location = Location();
          location.code = methodCall.arguments['code'];

          if (location.code == 200) {
            location.name = await methodCall.arguments['name'];
            location.latitude = await methodCall.arguments['latitude'];
            location.longitude = await methodCall.arguments['longitude'];
            location.address = await methodCall.arguments['address'];
          }

          if (locationController.listener.length > 0) {
            for (LocationCallBack locationCallBack
                in locationController.listener) {
              locationCallBack(location);
            }
          }
          break;
        case "2":
          break;
      }
    });

    return await tencChannel
        .invokeMethod("flutter_tenc_map_init", {"key": key});
  }

  static Future<Location> getLocation(int interval) async {
    var data = await tencChannel.invokeMethod(
        "flutter_tenc_map_getLocation", {"interval": interval.toDouble()});
    if (data == "error") {
      return null;
    }
    //print(data);
    Location location = Location();
    location.name = data['name'];
    location.latitude = data['latitude'];
    location.longitude = data['longitude'];
    location.address = data['address'];

    return location;
  }

  static stopLocation() async {
    return await tencChannel.invokeMethod("stopLocation");
  }

  static Future<String> get platformVersion async {
    final String version = await tencChannel.invokeMethod('getPlatformVersion');
    return version;
  }

  static bool distory() {
    locationController.listener.clear();
    locationController = null;
    stopLocation();
    return true;
  }
}
