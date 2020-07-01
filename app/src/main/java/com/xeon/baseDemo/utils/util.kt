package com.xeon.baseDemo.utils

import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.telephony.TelephonyManager
import com.xeon.baseDemo.App
import com.xeon.baseDemo.data.Location
import com.xeon.baseDemo.data.LocationStorage
import com.xeon.baseDemo.handler.GenerateKey
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import org.threeten.bp.format.DateTimeFormatter
import java.lang.Exception
import java.security.MessageDigest

fun ConnectivityManager.hasActiveConnection(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        activeNetworkInfo?.isConnected ?: false || allNetworks.firstOrNull { getNetworkInfo(it).state == NetworkInfo.State.CONNECTED } != null
    } else {
        allNetworkInfo.firstOrNull { it?.isConnected == true } != null
    }
}


fun Context.hasActiveConnection(): Boolean {
    return (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).hasActiveConnection()
}

fun Context.getConnectivityObservable(): Observable<Boolean> {
    return receiveBroadcasts(ConnectivityManager.CONNECTIVITY_ACTION)
        .map { hasActiveConnection() }
        .startWith(hasActiveConnection())
}

fun LocationStorage.toLocation(): Location {
    return Location(
        latitude,
        longitude,
        radius,
        altitude,
        speed,
        createTime,
        direction
    )
}

fun Context.receiveBroadcasts(vararg actions: String): io.reactivex.Observable<Intent> {
    return io.reactivex.Observable.create<Intent> { emitter ->
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                emitter.onNext(intent)
            }
        }

        val filter = IntentFilter().apply { actions.forEach(this::addAction) }
        registerReceiver(receiver, filter)

        emitter.setCancellable { unregisterReceiver(receiver) }
    }
}


val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")

data class Range<T : Comparable<T>>(
    val start: T,
    val end: T
) : Comparable<Range<T>> {

    override fun compareTo(other: Range<T>): Int {
        return start.compareTo(other.start)
    }

    fun contains(v: T): Boolean {
        return v >= start && v < end
    }
}


private val logger = MLog.getLog("RxJava")

fun <T> Observable<T>.logErrorAndForget(extraAction: (err: Throwable) -> Unit = {}): Observable<T> {
    return onErrorResumeNext { throwable: Throwable ->
        logger.e(throwable) { "Ignored error: " }
        extraAction(throwable)
        Observable.empty<T>()
    }
}

fun <T> Maybe<T>.logErrorAndForget(extraAction: (err: Throwable) -> Unit = {}): Maybe<T> {
    return onErrorResumeNext { throwable: Throwable ->
        logger.e(throwable) { "Ignored error: " }
        extraAction(throwable)
        Maybe.empty<T>()
    }
}

/***
 * 注意这次捕捉异常，会返回一次执行成功
 * ***/
fun Completable.logErrorAndForget(extraAction: (err: Throwable) -> Unit = {}): Completable {
    return onErrorResumeNext { throwable ->
        logger.e(throwable) { "Ignored error: " }
        extraAction(throwable)
        Completable.complete()
    }
}

private val generateKey: GenerateKey by lazy {
    return@lazy GenerateKey()
}


@SuppressLint("MissingPermission")
fun Context.getImeiAndICCID(any: String?): DeviceInfo {
    return try {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) tm.imei else tm.deviceId
        val iccid = tm.simSerialNumber
        val key = generateKey.generateKey(any ?: "apiKey")

        DeviceInfo(imei, iccid, key)
    } catch (e: Exception) {
        DeviceInfo(null, null, "error")
    }
}

fun Context.getImeiAndICCIDString(any: String? = null): String {
    return App.instance.objectMapper.writeValueAsString(getImeiAndICCID(any))
}


data class DeviceInfo(val imei: String?, val iccid: String?, val key: String)

fun String.md5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.hex()
}

fun ByteArray.hex(): String {
    return joinToString("") { "%02X".format(it) }
}

data class Req(val content: String)

fun String.toReq(): Req = Req(this)

fun List<Int>.toWeekDayList(): String {
    var str = ""
    forEach { item ->
        when (item) {
            1 -> str += "周一 "
            2 -> str += "周二 "
            3 -> str += "周三 "
            4 -> str += "周四 "
            5 -> str += "周五 "
            6 -> str += "周六 "
            7 -> str += "周日 "
        }
    }
    return str
}
