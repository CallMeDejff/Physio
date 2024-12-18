package com.dawidkubica.physio

import android.app.Application
import com.facebook.FacebookSdk.sdkInitialize
import com.facebook.appevents.AppEventsLogger
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class PhysioHiltApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(
                apiKey = "goog_ycjWjsIZNaIMizGeoavZYbpEHAW",
            ).build()
        )

        sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
    }
}