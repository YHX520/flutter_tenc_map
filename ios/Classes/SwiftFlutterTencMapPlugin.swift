import Flutter
import UIKit
import TencentLBS


public class SwiftFlutterTencMapPlugin: NSObject, FlutterPlugin {
    var location:TencentLBSLocationManager?
    var timer:Timer?
    var resultList:Array<FlutterResult>=Array()
    
   public static var channel:FlutterMethodChannel?
    
  public static func register(with registrar: FlutterPluginRegistrar) {
     channel = FlutterMethodChannel(name: "flutter_tenc_map", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterTencMapPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel!)
  }

  

  public func handle(_ call: FlutterMethodCall,result: @escaping FlutterResult) {
    print(call.method)
    switch call.method {
    case "getPlatformVersion":
        
        result("iOS " + UIDevice.current.systemVersion)
        
        break
    case "flutter_tenc_map_init":
        
        self.initMap(key:call.arguments, result: result)
        
        break
    case "flutter_tenc_map_getLocation":
       
        if timer != nil {
            timer!.invalidate()
            timer=nil
        }
        var interval=(call.arguments as! Dictionary<String,Any>)["interval"] as! Float
        interval=interval/1000
        resultList.append(result)
        timer=Timer.scheduledTimer(timeInterval: TimeInterval(interval), target:self, selector: #selector(startLocation), userInfo: nil, repeats: true)
        self.startLocation()
        
    case "stopLocation":
        
        self.stopLocation()
        
        break
    default:
        break
    }
   
  }
    
    //初始化定位
    public func initMap(key:Any?,result:FlutterResult){
        
      let args = key as! Dictionary<String, Any>
        
        print(args["key"]!,"ke")
        self.location=TencentLBSLocationManager.init()
        self.location?.apiKey=args["key"] as! String
        self.location?.requestWhenInUseAuthorization()
        
    }
    
    ///获取定位
    @objc public func startLocation(){
        
        self.location?.requestLocation(completionBlock: { (location:TencentLBSLocation?,e: Error?) in
            print(e)
            var map=Dictionary<String,Any>.init()
            
            if(e != nil){
                 map["code"]=(e! as NSError).code
            }else{
                map["name"]=location?.name
                map["latitude"]=location?.location.coordinate.latitude
                map["longitude"]=location?.location.coordinate.longitude
                map["address"]=location?.address
                map["code"]=200
            }
            
            for result in self.resultList{
                result(map)
            }
            self.resultList.removeAll()
            SwiftFlutterTencMapPlugin.channel?.invokeMethod("flutter_tenc_map_backLocation", arguments: map)
            
        })
    }
    
    //停止定位
   public func stopLocation() {
        self.location?.stopUpdatingLocation()
    if(timer != nil){
        timer?.invalidate()
    }
    }
    
}
