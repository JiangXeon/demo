package com.xeon.baseDemo.ui.base

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.xeon.baseDemo.utils.MLog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity() {
    private var disposables: CompositeDisposable? = null
    val logger: MLog by lazy { MLog.getLog(javaClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        supportActionBar?.hide()
        request()
    }

    override fun onStop() {
        super.onStop()

        disposables?.dispose()
        disposables = null
    }

    fun Disposable.bindToLifecycle(): Disposable {
        if (disposables == null) {
            disposables = CompositeDisposable()
        }

        disposables!!.add(this)
        return this
    }

    private fun request() {
        if (ActivityCompat.checkSelfPermission(
                this,
                permissions.first()
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限全部授予
            } else {
                Toast.makeText(this, "未给权限，本APP不能正常工作！", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    companion object {
        private const val REQUEST_CODE = 100

        private val permissions = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WAKE_LOCK
        )
    }
}