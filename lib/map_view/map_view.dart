import 'package:flutter/material.dart';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:flutter_tenc_map/model/location.dart';

import '../flutter_tenc_map.dart';

typedef void MapViewCreatedCallback(MapViewController controller);

class MapView extends StatefulWidget {
  MapView({this.mapViewCreatedCallback, Key key}) : super(key: key);
  final MapViewCreatedCallback mapViewCreatedCallback;
  @override
  State<StatefulWidget> createState() {
    return MapViewState();
  }
}

class MapViewState extends State<MapView> {
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Platform.isAndroid
        ? AndroidView(
            viewType: "plugins.tencmap.mapview/mapview",
            creationParamsCodec: StandardMessageCodec(),
            onPlatformViewCreated: _createView)
        : Container(
            child: UiKitView(viewType: "plugins.tencmap.mapview/mapview"));
  }

  void _createView(int id) {
    if (widget.mapViewCreatedCallback == null) {
      return;
    } else {
      widget.mapViewCreatedCallback(MapViewController.init(id));
    }
  }
}

class MapViewController {
  final MethodChannel _channel;

  MapViewController.init(int id)
      : _channel = new MethodChannel("plugins.tencmap.mapview_$id");

  Future<bool> showMyLocation({MyLocationStyle myLocationStyle}) {
    return _channel.invokeMethod("plugins.tencmap.mapview_setMyLocation",
        myLocationStyle != null ? myLocationStyle.toJson() : null);
  }

  Future<bool> setTagIcons(TagTip tagTip) {
    return _channel.invokeMethod("plugins.tencmap.mapview_setTagIcon", {
      "longitude": tagTip.longitude,
      "latitude": tagTip.latitude,
      "title": tagTip.title ?? "",
      "subTitle": tagTip.subTitle ?? ""
    });
  }
}
