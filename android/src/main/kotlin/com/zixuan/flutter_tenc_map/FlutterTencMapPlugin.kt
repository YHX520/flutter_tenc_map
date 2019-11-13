package com.zixuan.flutter_tenc_map

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.zixuan.flutter_tenc_map.mapView.MapViewFactory
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Handler

const val START=0
const val CREATED = 1
const val RESUMED = 3
const val STOPPED = 5
const val DESTROYED = 6

@RequiresApi(Build.VERSION_CODES.M)
class FlutterTencMapPlugin(newActivity: Activity, channel: MethodChannel, registrar: Registrar) : MethodCallHandler, TencentLocationListener {

    var tencentLocationRequest: TencentLocationRequest? = null

    var tencentLocationManager: TencentLocationManager? = null

    var activity: Activity? = null

    var channel: MethodChannel? = null;

    var location: TencentLocation? = null;
    var resultLista: ArrayList<Result>? = null;

    var result: Result? = null;





    init {
        activity = newActivity
        this.channel = channel
        this.resultLista = arrayListOf()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            registrar.addRequestPermissionsResultListener { code, _, grants ->
                if (code == 200 && grants[0] == PackageManager.PERMISSION_GRANTED) {
                    init()
                } else {
                    init()
                    result!!.success("");
                }
                return@addRequestPermissionsResultListener true
            }
        }


    }

    ///初始化定位管理器
    private fun init() {
        if (tencentLocationManager == null) {
            //Toast.makeText(activity!!, "已开启定位权限", Toast.LENGTH_LONG).show();
            tencentLocationRequest = TencentLocationRequest.create()
            tencentLocationManager = TencentLocationManager.getInstance(activity!!)
           // result!!.success("success")
        }

    }

    companion object :Application.ActivityLifecycleCallbacks{

        lateinit var registrar: Registrar

        private  var  activityState = AtomicInteger(0)

        private var registrarActivityHashCode: Int = 0


        @JvmStatic
        fun registerWith(registrar: Registrar) {
            this.registrar=registrar;
            registrar.platformViewRegistry().registerViewFactory("plugins.tencmap.mapview/mapview", MapViewFactory(registrar.messenger(), activityState))
            registrar.activity().application.registerActivityLifecycleCallbacks(this)
            val channel = MethodChannel(registrar.messenger(), "flutter_tenc_map")
            channel.setMethodCallHandler(FlutterTencMapPlugin(registrar.activity(), channel, registrar))

        }

        override fun onActivityPaused(activity: Activity) {
            if (activity.hashCode() != registrarActivityHashCode) {
                return
            }

        }

        override fun onActivityResumed(activity: Activity) {
            if (activity.hashCode() != registrarActivityHashCode) {
                return
            }
            activityState.set(RESUMED)
        }

        override fun onActivityStarted(activity: Activity) {
            if (activity.hashCode() != registrarActivityHashCode) {
                return
            }
            activityState.set(CREATED)
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (activity.hashCode() != registrarActivityHashCode) {
                return
            }
            activityState.set(DESTROYED)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
            if (activity.hashCode() != registrarActivityHashCode) {
                return
            }
        }

        override fun onActivityStopped(activity: Activity) {
            if (activity.hashCode() != registrarActivityHashCode) {
                return
            }
            activityState.set(STOPPED)
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (activity.hashCode() != registrarActivityHashCode) {
                return
            }

            activityState.set(CREATED)
        }


    }

    override fun onMethodCall(call: MethodCall, result: Result) {

        when (call.method) {
            "flutter_tenc_map_init" -> {

                this.result = result

                //初始化定位SDK
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CHANGE_WIFI_STATE)
                            != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
                        //开启定位权限,200是标识码
                        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CHANGE_WIFI_STATE), 200);
                    } else {
                        init()
                    }
                } else {
                    init()
                }

            }

            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "flutter_tenc_map_getLocation" -> {
                var interval: Long = 10000
                if (call.hasArgument("interval")) {
                    interval = ((call.arguments as HashMap<String, Any>)["interval"] as Double).toLong();
                }

               // print(interval);


                if (resultLista!!.size <= 0) {
                    tencentLocationRequest!!.interval = interval;
                    val error = tencentLocationManager!!.requestLocationUpdates(tencentLocationRequest!!, this);
                    if (error != 0) {
                        resultLista!!.clear();
                        result.success("error")
                    }
                }
                //获取一次定位
                this.resultLista!!.add(result)
            }
            "stopLocation" -> {

                android.os.Handler().postDelayed(Runnable {
                    tencentLocationManager!!.removeUpdates(this@FlutterTencMapPlugin)
                }, 100)
                result.success(true)
            }
            else -> {

            }
        }

    }


    /**
     * 状态发生改变
     * @param p0 String
     * @param p1 Int
     * @param p2 String
     */
    override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {
        // Log.d("状态", p0);
    }

    //定位发生改变
    override fun onLocationChanged(location: TencentLocation, error: Int, reason: String) {
        //System.out.println("定位"+location!!.address)
        //Toast.makeText(activity, reason + location.address, Toast.LENGTH_LONG).show();

        if (error == 0) {
            val map = HashMap<String, Any>();
            this.location = location
            map.put("name", location.name)
            map.put("latitude", location.latitude)
            map.put("longitude", location.longitude)
            map.put("address", location.address)
            map.put("code", 200)

            for (result in resultLista!!.iterator()) {
                result.success(map)
            }
            resultLista!!.clear();

            if (channel != null) {
                // Log.d("调用", "location");
                channel!!.invokeMethod("flutter_tenc_map_backLocation", map)
            }
        } else {
            val map = HashMap<String, Any>();
            this.location = location

            var code = 4



            when (error) {
                1 -> {
                    code = 2
                }
                2 -> {
                    code = 1
                }
                4 -> {
                    code = 0
                }
            }

            map.put("code", code)
            for (result in resultLista!!.iterator()) {
                result.success(map)
            }
            resultLista!!.clear();

            if (channel != null) {
                // Log.d("调用", "location");
                channel!!.invokeMethod("flutter_tenc_map_backLocation", map)
            }

        }

    }




}
