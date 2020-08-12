package com.yieldlab.yieldprobe_sample_application

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

/**
 * AppGlobal class to enable multidex support.
 */
class AppGlobal : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // enable multidex
        MultiDex.install(this)
    }
}
