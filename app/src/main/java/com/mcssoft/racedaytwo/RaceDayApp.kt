package com.mcssoft.racedaytwo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RaceDayApp : Application() {

    override fun onCreate() {
        super.onCreate()

//        // https://greenrobot.org/eventbus/documentation/subscriber-index/
//        EventBus.builder().addIndex(MyEventBusIndex()).installDefaultEventBus()
    }
}