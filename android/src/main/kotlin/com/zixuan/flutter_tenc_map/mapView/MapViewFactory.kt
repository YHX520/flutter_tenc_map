package com.zixuan.flutter_tenc_map.mapView

import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MessageCodec
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author  : yanghuaxuan / yanghuaxuan@seerbigdata.com
 * @date    : 2019-09-09
 * @version : 1.0.0
 */
class MapViewFactory(messagess: BinaryMessenger?,private val activityState: AtomicInteger) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    private var messages: BinaryMessenger?=null

    init {
        this.messages=messagess;
    }

    override fun create(context: Context, id: Int, oj: Any?): PlatformView {

        val  view=MapView(context,messages!!, id,activityState);
         view.setup();

        return  view;
    }
}