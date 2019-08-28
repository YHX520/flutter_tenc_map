import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_tenc_map/flutter_tenc_map.dart';

void main() async {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  Location location;

  LocationController locationController = LocationController();

  @override
  void initState() {
    super.initState();
    initPlatformState();
    FlutterTencMap.locationController.listener.add((location) {
      this.location = location;
      setState(() {});
    });
    FlutterTencMap.init("fdsa");
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterTencMap.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
            child: Column(
          children: <Widget>[
            Text('Running on: $_platformVersion\n'),
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
            RaisedButton(
              onPressed: () {
                stopLocation();
              },
              child: Text("停止获取"),
            ),
          ],
        )),
      ),
    );
  }

  getLocation() async {
    location = await FlutterTencMap.getLocation(10000);
    setState(() {});
  }

  void stopLocation() async {
    bool isOK = await FlutterTencMap.stopLocation();
    if (isOK) {
      print("停止成功");
    }
  }

  @override
  void dispose() {
    FlutterTencMap.distory();
    print("调用停止");
    super.dispose();
  }
}
