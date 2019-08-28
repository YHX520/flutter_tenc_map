#import "FlutterTencMapPlugin.h"
#import <flutter_tenc_map/flutter_tenc_map-Swift.h>

@implementation FlutterTencMapPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterTencMapPlugin registerWithRegistrar:registrar];
}
@end
