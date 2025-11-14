package com.aryanspatel.droidwire

import android.app.Application
import com.aryanspatel.droidwire.core.notif.NewsChannel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DroidWireApplication : Application(){
    override fun onCreate(){
        super.onCreate()
        NewsChannel.init(this)
    }
}