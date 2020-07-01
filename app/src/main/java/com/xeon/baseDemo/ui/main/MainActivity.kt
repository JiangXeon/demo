package com.xeon.baseDemo.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.os.Build
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.Toast
import com.xeon.baseDemo.App
import com.xeon.baseDemo.ui.base.BaseActivity
import com.xeon.baseDemo.ui.map.MapActivity
import com.xeon.baseDemo.utils.getImeiAndICCIDString
import com.xeon.baseDemo.utils.logErrorAndForget
import com.xzkj.location.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import kotlin.math.log


class MainActivity : BaseActivity() {
    private val jump: Button by lazy { findViewById<Button>(R.id.jump) }
    private val http: Button by lazy { findViewById<Button>(R.id.http) }
    private val dialog: Button by lazy { findViewById<Button>(R.id.dialog) }
    private val jni: Button by lazy { findViewById<Button>(R.id.jni) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewInit()
    }

    private fun viewInit() {
        jump.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
        http.setOnClickListener {
            App.instance.api.testBaidu()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .logErrorAndForget { logger.e { it } }
                .subscribe {
                    logger.i { it }
                }
                .bindToLifecycle()
        }
        dialog.setOnClickListener {
            onClickShowDevice()
        }

        jni.setOnClickListener {
            Toast.makeText(this, getImeiAndICCIDString("1212312"), Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("MissingPermission")
    private fun onClickShowDevice() {
        try {
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) tm.imei else tm.deviceId
            val iccid = tm.simSerialNumber
            val version = applicationContext
                .packageManager
                .getPackageInfo(packageName, 0).versionName

            AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_logo)
                .setTitle("设备信息")
                .setMessage(" IMEI:$imei \n ICCID:$iccid \n 版本号：$version").setPositiveButton(
                    "知道了"
                ) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()

        } catch (e: Exception) {
        }
    }


}
