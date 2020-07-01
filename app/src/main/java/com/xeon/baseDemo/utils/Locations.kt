package com.xeon.baseDemo.utils

import com.baidu.location.*
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.xeon.baseDemo.App
import com.xzkj.location.BuildConfig
import com.xeon.baseDemo.data.Location
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

object Locations {
    private var logger = MLog.getLog("Locations")

    private val bdClient: LocationClient by lazy {
        LocationClient(App.instance).apply {
            locOption = LocationClientOption().apply {
                //在一些未知的环境下，仅仅GPS时定不了位，这个判断交由地图api自己来判断
                locationMode = LocationClientOption.LocationMode.Hight_Accuracy
                coorType = "bd09ll"
                enableSimulateGps = BuildConfig.DEBUG
//                    openGps=true
                //默认值
                scanSpan = miniScan.toInt()
                setNeedDeviceDirect(true)
                isNeedAltitude = true
                isIgnoreKillProcess = true
            }

        }
    }

    @Volatile
    private var currentLocationWork = 0
    //单位是毫秒值
    private var miniScan = 10000L

    val unifyLocation = BehaviorSubject.create<Location>()
    private val listener = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(loc: BDLocation) {
            logger.i { "原始数据 ${bdClient.locOption.scanSpan} ：" + loc.latitude + "  " + loc.longitude }
            if ((loc.latitude < 0.0 || loc.latitude > 0.000001 || loc.longitude < 0.0 || loc.longitude > 0.000001) && unifyLocation.value.let {
                    it == null || it.createTime + miniScan < System.currentTimeMillis() + 1000
                })
                unifyLocation.onNext(
                    Location(
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        radius = loc.radius.toInt(),
                        altitude = loc.altitude.toInt(),
                        speed = loc.speed.toInt(),
                        createTime = System.currentTimeMillis(),
                        direction = loc.direction
                    )
                )
        }

        override fun onConnectHotSpotMessage(var1: String, var2: Int) {
            logger.i { "onConnectHotSpotMessage $var1 $var2" }
        }

        override fun onLocDiagnosticMessage(var1: Int, var2: Int, var3: String) {
            logger.i { "onConnectHotSpotMessage $var1 $var2 $var3" }
        }
    }

    private fun setScanSpan(minTimeMills: Long) {
        bdClient.locOption.setScanSpan(minTimeMills.toInt())
    }

    //获取一个连续的定位信息
    fun requestLocationUpdate(miniTime: Long): Observable<Location> {
        logger.i { "请求一个定位 间隔:$miniTime" }
        return Observable.create<Location> { em ->
            AndroidSchedulers.mainThread().scheduleDirect {
                unifyLocation.subscribe(em::onNext)
                em.setCancellable { AndroidSchedulers.mainThread().scheduleDirect { stop() } }
                miniScan = miniTime
                setScanSpan(miniTime)
                bdClient.start()
                setBDLocationListener()
            }
        }
    }

    private fun stop() {
        bdClient.stop()
        bdClient.unRegisterLocationListener(listener)
        miniScan = 10000
    }

    private fun setBDLocationListener() {
        bdClient.registerLocationListener(listener)
    }

    fun requestSingleLocationUpdate(accurate: Boolean = false): Single<Location> {
        logger.i { "Requesting single location" }
        return unifyLocation.value.let {
            if (accurate.not() &&
                it != null &&
                //在粗略模式下一分钟内的定位数据可信
                System.currentTimeMillis() - it.createTime < 60 * 1000
            )
                Single.just(it)
            else
                requestLocationUpdate(1000L).firstOrError()
        }

    }

}

fun BaiduMap.receiveMapStatus(): Observable<MapStatus> {
    return Observable.create { emitter ->
        val listener = object : BaiduMap.OnMapStatusChangeListener {

            override fun onMapStatusChangeStart(p0: MapStatus) {
                emitter.onNext(p0)
            }

            override fun onMapStatusChangeStart(p0: MapStatus, p1: Int) {
                emitter.onNext(p0)
            }

            override fun onMapStatusChange(status: MapStatus) {
                emitter.onNext(status)
            }

            override fun onMapStatusChangeFinish(p0: MapStatus) {
                emitter.onNext(p0)
            }
        }
        setOnMapStatusChangeListener(listener)
        emitter.setCancellable {
            setOnMapStatusChangeListener(null)
        }
    }
}

//根据轨迹原始数据计算中心坐标和缩放级别，并为地图设置中心坐标和缩放级别。
fun BaiduMap.setTraceZoom(latLngs: List<com.baidu.mapapi.model.LatLng>) {
    var resd: com.baidu.mapapi.model.LatLng
    if (latLngs.isNotEmpty()) {
        var maxLng = latLngs[0].longitude
        var minLng = latLngs[0].longitude
        var maxLat = latLngs[0].latitude
        var minLat = latLngs[0].latitude
        for (usr in latLngs) {
            resd = usr
            if (resd.longitude > maxLng) maxLng = resd.longitude
            if (resd.longitude < minLng) minLng = resd.longitude
            if (resd.latitude > maxLat) maxLat = resd.latitude
            if (resd.latitude < minLat) minLat = resd.latitude
        }
        val cenLng = (maxLng + minLng) / 2
        val cenLat = (maxLat + minLat) / 2
        val zoom = getZoom(maxLng, minLng, maxLat, minLat)

        var msu = MapStatusUpdateFactory.newLatLng(
            com.baidu.mapapi.model.LatLng(cenLat, cenLng)
        )
        setMapStatus(msu)
        msu = MapStatusUpdateFactory.zoomTo(zoom)
        setMapStatus(msu)
    } else {
        //没有坐标，显示全中国
        //map.centerAndZoom(new BMap.Point(103.388611,35.563611), 5);
    }
}

//根据经纬极值计算绽放级别。
fun getZoom(maxLng: Double, minLng: Double, maxLat: Double, minLat: Double): Float {
    val zoom = intArrayOf(
        50,
        100,
        200,
        500,
        1000,
        2000,
        5000,
        10000,
        20000,
        25000,
        50000,
        100000,
        200000,
        500000,
        1000000,
        2000000
    )//级别18到3。
    //        var pointA = new BMap.Point(maxLng,maxLat);  // 创建点坐标A
    //        var pointB = new BMap.Point(minLng,minLat);  // 创建点坐标B
    //var distance = map.getDistance(pointA,pointB).toFixed(1);  //获取两点距离,保留小数点后两位
    val distance = getDistance(maxLat, maxLng, minLat, minLng)
    var i = 0
    val zoomLen = zoom.size
    while (i < zoomLen) {
        if (zoom[i] - distance > 0) {
            return (18 - i + 4).toFloat()//之所以会多4，是因为地图范围常常是比例尺距离的10倍以上。所以级别会增加5。
        }
        i++
    }
    return 0f
}

private fun getDistance(lat_a: Double, lng_a: Double, lat_b: Double, lng_b: Double): Double {
    val pk = 180 / 3.14169
    val a1 = lat_a / pk
    val a2 = lng_a / pk
    val b1 = lat_b / pk
    val b2 = lng_b / pk
    val t1 = cos(a1) * cos(a2) * cos(b1) * cos(b2)
    val t2 = cos(a1) * sin(a2) * cos(b1) * sin(b2)
    val t3 = sin(a1) * sin(b1)
    val tt = acos(t1 + t2 + t3)
    return 6371000 * tt
}