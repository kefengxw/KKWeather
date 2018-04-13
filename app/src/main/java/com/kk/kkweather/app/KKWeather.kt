package com.kk.kkweather.app

import android.app.Application
import com.kk.kkweather.util.LogUtil
import org.litepal.LitePal

/**
 * Created by xxnfd on 25/03/2018.
 */
class KKWeatherApp : Application() {

    companion object {
        lateinit var instance: KKWeatherApp
        //private set
    }

    override fun onCreate() {
        super.onCreate()
        globalInit()
    }

    private fun globalInit() {
        instance = this
        LitePal.initialize(this)

        LitePal.getDatabase()

        //better to define the log level here
        //initfont,initxxx init all kinds of system parm, for example the unit, the size
        LogUtil.i("KWeatherApp", "globalInit")
    }
}