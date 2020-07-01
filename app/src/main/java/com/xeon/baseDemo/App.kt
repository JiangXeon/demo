package com.xeon.baseDemo

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.baidu.mapapi.SDKInitializer
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule
import com.jakewharton.threetenabp.AndroidThreeTen
import com.xeon.baseDemo.api.AppApi
import com.xeon.baseDemo.db.Storage
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import com.xeon.baseDemo.handler.ReceiverInit
import com.xzkj.location.R


class App : Application() {
    lateinit var api: AppApi

    //数据库 组件
    val storage: Storage by lazy { Storage(this) }

    val sp: SharedPreferences by lazy { getSharedPreferences("location", Context.MODE_PRIVATE) }
    val objectMapper: ObjectMapper by lazy {
        ObjectMapper().apply {
            registerModule(JsonOrgModule())
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
            configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        }
    }

    override fun onCreate() {
        super.onCreate()

        //如果是百度地图的单独进程则直接跳过初始化流程
        if (getProcessName(applicationContext)!!.lastIndexOf("bdlocation") > 0) {
            return
        }
        SDKInitializer.initialize(applicationContext)
        instance = this
        AndroidThreeTen.init(this)
        initApi()

        ReceiverInit.instance
    }

    fun initApi() {
        var baseUrl = getString(R.string.default_server)

        if (!sp.getBoolean("default", true))
            baseUrl = sp.getString(
                "customUrl", baseUrl
            ) ?: baseUrl


        Log.w("initApi", baseUrl)
        api = Retrofit.Builder()
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory
                    .createWithScheduler(Schedulers.io())
            )
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .baseUrl(baseUrl)
            .build()
            .create(AppApi::class.java)
    }

    private fun getProcessName(context: Context): String? {
        val pid = android.os.Process.myPid()
        val mActivityManager = context
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in mActivityManager
            .runningAppProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.processName
            }
        }
        return null
    }

    companion object {
        @JvmStatic
        lateinit var instance: App
            private set
    }

}