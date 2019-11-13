import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_tenc_map/flutter_tenc_map.dart';

import 'map_page.dart';

void main() async {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    FlutterTencMap.init("5J2BZ-RKD63-6WU3E-YM6GU-COWVQ-ZSFS5");

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(home: MainPage());
  }
}

class MainPage extends StatefulWidget {
  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  String _platformVersion = 'Unknown';

  Location location;

  LocationController locationController = LocationController();

  String status = "定位中...";

  @override
  void initState() {
    super.initState();
    FlutterTencMap.locationController.listener.add((location) {
      this.location = location;
      switch (location.code) {
        case 0:
          status = "无法定位";
          break;
        case 1:
          status = "缺少权限";
          break;
        case 2:
          status = "网络错误";
          break;
        case 3:
          status = "无法获取方向";
          break;
        case 4:
          status = "未知错误";
          break;
        case 200:
          status = "正常";
          break;
      }
      setState(() {});
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
      ),
      body: Center(
          child: Column(
        children: <Widget>[
          RaisedButton(
            onPressed: () {
              getLocation();
            },
            child: Text("获取位置"),
          ),
          Text("位置为:" +
              (location == null
                  ? "未定位"
                  : (location.address ?? "") +
                      ("经度：${location.longitude} ") +
                      "纬度：${location.latitude}")),
          Text("状态：$status"),
          RaisedButton(
            onPressed: () {
              stopLocation();
            },
            child: Text("停止获取"),
          ),
          RaisedButton(
            onPressed: () {
              Navigator.push(
                  context, MaterialPageRoute(builder: (_) => MapPage()));
            },
            child: Text("打开地图"),
          )
        ],
      )),
    );
  }

  @override
  void dispose() {
    FlutterTencMap.distory();
    print("调用停止");
    super.dispose();
  }

  getLocation() async {
    location = await FlutterTencMap.getLocation(2000);
    setState(() {});
  }

  void stopLocation() async {
    bool isOK = await FlutterTencMap.stopLocation();
    if (isOK) {
      print("停止成功");
    }
  }
}
