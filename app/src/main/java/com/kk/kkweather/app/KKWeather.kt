package com.kk.kkweather.app

import android.app.Application
import com.kk.kkweather.util.LogUtil
import org.litepal.LitePal

/**
 * Created by xxnfd on 25/03/2018.
 */
class KKWeatherApp : Application(){

    companion object {
        lateinit var instance : KKWeatherApp
//    private set
    }

    override fun onCreate() {
        super.onCreate()

        globalInit()
    }

    private fun globalInit() {
        instance = this
        LitePal.initialize(this)

        LitePal.getDatabase()

//better to define the log level here可以定义自己的log方法和类
//        initfont
//        initxxx   进行各种计算，比如屏幕的分辨率等等，屏幕的长和宽，从而决定button等高度，至少决定最小单位等等，比如1，代表一个单位
//        inityyy
        LogUtil.i("KWeatherApp", "globalInit")

    }
}