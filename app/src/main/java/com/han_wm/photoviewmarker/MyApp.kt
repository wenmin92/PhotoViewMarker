package com.han_wm.photoviewmarker

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Resources
import timber.log.Timber

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        context = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set
        val resources: Resources by lazy { context.resources }
    }
}