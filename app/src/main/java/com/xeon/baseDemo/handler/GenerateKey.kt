package com.xeon.baseDemo.handler

class GenerateKey {

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun generateKey(str: String): String

    init {
        System.loadLibrary("native-lib")
    }
}