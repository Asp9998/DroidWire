package com.aryanspatel.droidwire.core.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.Context

object NewsChannel{
    const val ANDROID = "news_android"
    const val JETPACK = "news_jetpack"
    const val KOTLIN =  "news_kotlin"
    const val OFFICIAL = "news_official"

    fun init(context: Context){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channels = listOf(
            NotificationChannel(ANDROID, "Android News", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "Android platform/tools updates" },
            NotificationChannel(JETPACK, "Jetpack News", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "Jetpack libraries & releases" },
            NotificationChannel(KOTLIN, "Kotlin News", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "Kotlin & Compose updates" },
            NotificationChannel(OFFICIAL, "Official Blog", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "Android Developers Blog posts" }
        )
        val nm = context.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannels(channels)

    }
}