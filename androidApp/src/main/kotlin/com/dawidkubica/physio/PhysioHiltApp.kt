package com.dawidkubica.physio

import android.app.Application
import com.facebook.FacebookSdk.sdkInitialize
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class PhysioHiltApp : Application() {
    override fun onCreate() {
        super.onCreate()
        sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
    }
}