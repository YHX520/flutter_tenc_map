import 'package:flutter_tenc_map/model/location.dart';
import 'package:flutter/material.dart';

typedef LocationCallBack = void Function(Location location);

class LocationController {
  List<LocationCallBack> listener = List();
}
