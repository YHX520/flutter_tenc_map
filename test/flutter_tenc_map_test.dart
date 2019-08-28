import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_tenc_map/flutter_tenc_map.dart';

void main() {
  const MethodChannel channel = MethodChannel('flutter_tenc_map');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await FlutterTencMap.platformVersion, '42');
  });
}
