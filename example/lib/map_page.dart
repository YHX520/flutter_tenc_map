import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_tenc_map/flutter_tenc_map.dart';

class MapPage extends StatefulWidget {
  @override
  _MapViewState createState() => _MapViewState();
}

class _MapViewState extends State<MapPage> {
  MapViewController mapViewController;

  @override
  void initState() {
    Timer(Duration(milliseconds: 3000), () {
      print(mapViewController.toString() + "controller");
      if (mapViewController != null) {
        MyLocationStyle myLocationStyle = MyLocationStyle();
        myLocationStyle.fillColor = FillColor(a: 20, r: 255, g: 45, b: 120);
        mapViewController.showMyLocation(myLocationStyle: myLocationStyle);
      }
    });

    Timer(Duration(milliseconds: 5000), () {
      //22.982586,113.767906
      mapViewController
          .setTagIcons(TagTip(longitude: 113.767906, latitude: 22.982586));
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(body: Container(
      child: MapView(
        mapViewCreatedCallback: (controller) {
          mapViewController = controller;
        },
      ),
    ));
  }

  @override
  void dispose() {
    super.dispose();
  }
}
