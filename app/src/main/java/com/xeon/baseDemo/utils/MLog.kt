package com.xeon.baseDemo.utils

import android.util.Log

class MLog private constructor(private val tag: String) {
    fun d(func: () -> String) {
        Log.d(tag, func())
    }

    fun d(msg: String) {
        Log.d(tag, msg)
    }

    fun v(func: () -> String) {
        Log.v(tag, func())
    }

    fun i(func: () -> Any) {
        Log.i(tag, func().toString())
    }

    fun info(tag: String, msg: Any? = null, exra: Any? = null) {
        Log.i(tag, msg.toString() + exra.toString())
    }


    fun w(func: () -> String) {
        Log.w(tag, func())
    }


    fun e(func: () -> Any?) {
        Log.e(tag, func().toString())
    }

    fun error(msg: String) {
        Log.e(tag, msg)
    }


    fun e(e: Throwable?, func: () -> String) {
        e?.printStackTrace()
        Log.e(tag, func() + e?.message)
    }


    companion object {
        fun getLog(tag: String): MLog {
            return synchronized(MLog::class.java) { MLog(tag) }
        }

        fun getLog(tag: Class<*>): MLog {
            return synchronized(MLog::class.java) { MLog(tag.simpleName) }
        }
    }
}