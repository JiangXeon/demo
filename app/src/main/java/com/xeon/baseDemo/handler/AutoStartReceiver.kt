package com.xeon.baseDemo.handler

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xeon.baseDemo.utils.MLog

class AutoStartReceiver : BroadcastReceiver() {
    val logger = MLog.getLog("AutoStartReceiver")
    @SuppressLint("StringFormatMatches")
    override fun onReceive(context: Context?, intent: Intent?) {
    }
}