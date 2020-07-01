package com.xeon.baseDemo.ui.map

import android.os.Bundle
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.xeon.baseDemo.ui.base.BaseActivity
import com.xeon.baseDemo.utils.Locations
import com.xzkj.location.R
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MapActivity : BaseActivity() {
    private val mMapView: MapView by lazy { findViewById<MapView>(R.id.map) }
    private val mBaiduMap: BaiduMap by lazy { mMapView.map }
    private var first = true

    override fun onStart() {
        super.onStart()
        first = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity)
        mapInit()
    }

    private fun mapInit() {
        mBaiduMap.isMyLocationEnabled = true

        Locations.unifyLocation
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(2000, TimeUnit.MILLISECONDS)
            .subscribe {
                val locData = MyLocationData.Builder()
                    .accuracy((it.radius ?: 1).toFloat())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(it.direction ?: 1.0f)
                    .latitude(it.latitude)
                    .longitude(it.longitude).build()

                val builder = MapStatus.Builder()
                mBaiduMap.setMyLocationData(locData)
                if (first) {
                    first = false
                    builder.zoom(18.0f)
                    builder.target(LatLng(it.latitude, it.longitude))
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
                }
            }.bindToLifecycle()

    }

    override fun onResume() {
        super.onResume()
        //在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }
}