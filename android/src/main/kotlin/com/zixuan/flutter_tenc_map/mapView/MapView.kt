package com.zixuan.flutter_tenc_map.mapView

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.tencentmap.mapsdk.maps.*
import com.tencent.tencentmap.mapsdk.maps.MapView
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions
import com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle
import com.zixuan.flutter_tenc_map.*
import com.zixuan.flutter_tenc_map.FlutterTencMapPlugin.Companion.registrar
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import java.util.concurrent.atomic.AtomicInteger


/**
 * @author  : yanghuaxuan
 * @date    : 2019-09-09
 * @version : 1.0.0
 */

class MapView(context: Context?,messages:BinaryMessenger?,id:Int?,val activityState: AtomicInteger):PlatformView, MethodChannel.MethodCallHandler, Application.ActivityLifecycleCallbacks {

    //标记记是否销毁
    private var disposed = false
    //
    private val registrarActivityHashCode: Int = registrar.activity().hashCode()


    fun setup() {
        when (activityState.get()) {
            STOPPED -> {
                mapView!!.onStop()
            }
            RESUMED -> {
                mapView!!.onResume()
            }
            START ->{
                mapView!!.onStart()
                mapView!!.onResume()//不加这句，地图会卡住
            }
            DESTROYED -> {
                mapView!!.onDestroy()
            }
            else -> throw IllegalArgumentException("Cannot interpret " + activityState.get() + " as an activity activityState")
        }




        var eventSink: EventChannel.EventSink? = null
        val markerClickedEventChannel = EventChannel(registrar.messenger(), "event")
        markerClickedEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(p0: Any?, sink: EventChannel.EventSink?) {
                eventSink = sink
            }

            override fun onCancel(p0: Any?) {}
        })


        // 注册生命周期
        registrar.activity().application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityPaused(activity: Activity?) {
        if (disposed || activity!!.hashCode() != registrarActivityHashCode) {
            return
        }
    }

    override fun onActivityResumed(activity: Activity?) {
        if (disposed || activity!!.hashCode() != registrarActivityHashCode) {
            return
        }
        mapView!!.onResume()
    }

    override fun onActivityStarted(activity: Activity?) {
        if (disposed || activity!!.hashCode() != registrarActivityHashCode) {
            return
        }
        mapView!!.onStart()
    }

    override fun onActivityDestroyed(activity: Activity?) {
        if (disposed) {
            return
        }
        disposed = true
        mapView!!.onDestroy()

        registrar.activity().application.unregisterActivityLifecycleCallbacks(this)
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
        if (disposed || activity!!.hashCode() != registrarActivityHashCode) {
            return
        }
        mapView!!.onStop()

    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

    }


    private  var  mapView:MapView?=null

    private  var textView:TextView?=null

    private  var tencentMapOptions:TencentMapOptions= TencentMapOptions()

    private  var  channel:MethodChannel?=null;

    private  var tencentMap:TencentMap;

    private  var mContext:Context?=null;

    init {
        channel= MethodChannel(messages,"plugins.tencmap.mapview_"+id);
        channel!!.setMethodCallHandler(this);
        mContext=context

        mapView=MapView(context,tencentMapOptions)
         tencentMap=mapView!!.map
        tencentMap.isMyLocationEnabled=true
        tencentMap.mapStyle=TencentMap.MAP_TYPE_NONE


    }

    override fun getView(): View {
        Log.d("返回页面","-----")
        return  mapView!!;
    }

    override fun dispose() {

    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when(call.method){
            "plugins.tencmap.mapview_setMyLocation"->{

                showMyLocation(call,result)
            }
            "plugins.tencmap.mapview_setTagIcon"->{
                Log.d("定位信息","接收标记")
                setTagIcon(call,result)
            }
        }

    }

    ///显示本地位置
    private fun showMyLocation(call: MethodCall, result: MethodChannel.Result) {
       if(!tencentMap.isDestroyed){
           tencentMap.isMyLocationEnabled=true


           if(call.arguments!=null&&(call.arguments as Map<String, Any>)["fillColor"]!=null){
               var fillColor=(call.arguments as Map<String,Any>)["fillColor"] as Map<String,Int>
               Log.d("数据",fillColor["a"]!!.toString());
               tencentMap.setMyLocationStyle(MyLocationStyle().fillColor(Color.argb(fillColor["a"]!!,fillColor["r"]!!,fillColor["g"]!!,fillColor["b"]!!)))
           }

           tencentMap.setLocationSource(MyLocationSource(mContext))

           result.success(true);
       }else{
           result.success(false);
       }
    }

    private  fun setTagIcon(call: MethodCall, result: MethodChannel.Result){

        val arguments= call.arguments as Map<String,Any>;

        var longitude=arguments["longitude"] as Double;

        var latitude=arguments["latitude"] as Double;

        var title=arguments["title"] as String;

        var subTitle=arguments["subTitle"] as String;



        var marker= tencentMap.addMarker(MarkerOptions(LatLng(latitude,longitude)).title(title).snippet(subTitle).draggable(true).anchor(0.5f,0.5f));
        tencentMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(LatLng(latitude,longitude),18f,0f,0f)))
        result.success(true)
    }
}



 //位置源类
class  MyLocationSource(context: Context?):LocationSource, TencentLocationListener {

    private var mContext: Context? = null
    private var mChangedListener: LocationSource.OnLocationChangedListener? = null
    private var locationManager: TencentLocationManager? = null
    private var locationRequest: TencentLocationRequest? = null
    
    
    init {
        mContext=context
        locationManager = TencentLocationManager.getInstance(mContext)
        locationRequest = TencentLocationRequest.create()
        locationRequest!!.setInterval(5000)
    }
    
    override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {
       
    }

     ///定位成功后回调
    override fun onLocationChanged(location: TencentLocation?, error: Int, reson: String?) {
       if(error==TencentLocation.ERROR_OK&&locationManager!=null){
           val location2 = Location(location!!.getProvider())
           location2.setLatitude(location!!.getLatitude())
           location2.setLongitude(location!!.getLongitude())
           location2.setAccuracy(location!!.getAccuracy())
           // 定位 sdk 只有 gps 返回的值才有可能获取到偏向角
           location2.setBearing(location!!.getBearing())
           mChangedListener!!.onLocationChanged(location2)
       }
    }

     ///销毁后调用
    override fun deactivate() {
        locationManager!!.removeUpdates(this)
        mContext = null
        locationManager = null
        locationRequest = null
        mChangedListener = null
    }

     ///获取定位时调用
    override fun activate(listenner: LocationSource.OnLocationChangedListener?) {
        mChangedListener=listenner;
        locationManager!!.requestLocationUpdates(locationRequest, this);
        
    }

}