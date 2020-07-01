package com.xeon.baseDemo.handler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import com.xeon.baseDemo.App
import com.xzkj.location.R

class ReceiverInit(val context: Context) {
    init {
        val res = context.resources
        registerReceiver(AutoStartReceiver(), res.getStringArray(R.array.auto_start))
    }

    private fun registerReceiver(receiver: BroadcastReceiver, actions: Array<String>, category: String? = null) {
        val intentFilter = IntentFilter().apply {
            priority = 65536
            category?.also(this::addCategory)
            actions.forEach(this::addAction)
        }
        context.registerReceiver(receiver, intentFilter)
    }

    companion object {
        val instance: ReceiverInit by lazy {
            ReceiverInit(App.instance)
        }
    }
}