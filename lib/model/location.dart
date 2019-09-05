class Location {
  ///纬度
  double latitude;

  ///经度
  double longitude;

  ///地名
  String name;

  ///地址
  String address;

  ///海拔
  double altitude;

  ///  code值说明
  ///
  /// 0 表示目前位置未知，但是会一直尝试获取
  /// 1 表示定位权限被禁止
  /// 2 表示网络错误
  /// 3 表示朝向无法确认 开启转向才会用到
  /// 4 表示未知错误
  /// 200 表示定位成功
  int code = 1;

  double getLatitude() {
    return latitude;
  }

  double getLongitude() {
    return longitude;
  }

  double getAltitude() {
    return altitude;
  }

  String getName() {
    return name;
  }

  String getAddress() {
    return address;
  }

  int getCode() {
    return code;
  }
}
