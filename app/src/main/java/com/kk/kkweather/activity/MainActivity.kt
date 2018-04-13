package com.kk.kkweather.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.preference.PreferenceManager
import com.kk.kkweather.util.LogUtil
import com.kk.kkweather.R
import kotlinx.android.synthetic.main.area_main_fragment.*


class MainActivity : AppCompatActivity() {

    var ctx = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (true == tryToStartWeatherActivity()) {
            LogUtil.i("MainActivity", "Successful to start WeatherActivity")
            return
        }

        setContentView(R.layout.amframelayout)

        //bug to fix, whilte backgournd
        if (AreaMainFragment.activityType == AreaMainFragment.ACTIVITY_TYPE_AREA_MAIN_ACTIVITY) {
            area_main_title_backbutton.setImageResource(R.drawable.ic_arrow_back_white_48dp)
            area_main_title_backbutton.setColorFilter(Color.argb(255, 66, 133, 244))
        }
    }

    private fun tryToStartWeatherActivity(): Boolean {
        //later, can add the GPS feature here, to decide current location
        var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        var wid = prefs.getString("weatherId", null)
        var cou = prefs.getString("country", null)
        var ret = false
        if (wid != null) {
            WeatherActivity.actionStartWA(ctx, cou, wid)
            AreaMainFragment.activityType = AreaMainFragment.ACTIVITY_TYPE_WEATHER_DRAWER_ACTIVITY
            this.finish()//close area main activity
            ret = true
        }
        AreaMainFragment.activityType = AreaMainFragment.ACTIVITY_TYPE_AREA_MAIN_ACTIVITY
        return ret
    }

//    override fun onStart() {
//        super.onStart()
//        //Toast.makeText(ctx, "Hi MainActivity onStart", Toast.LENGTH_SHORT).show()
//        LogUtil.i("KW-MainActivity", "MainActivity onStart")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        //Toast.makeText(ctx, "Hi MainActivity onResume", Toast.LENGTH_SHORT).show()
//        LogUtil.i("KW-MainActivity", "MainActivity onResume")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        //Toast.makeText(ctx, "Hi MainActivity onPause", Toast.LENGTH_SHORT).show()
//        LogUtil.i("KW-MainActivity", "MainActivity onPause")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        //Toast.makeText(ctx, "Hi MainActivity onStop", Toast.LENGTH_SHORT).show()
//        LogUtil.i("KW-MainActivity", "MainActivity onStop")
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        //Toast.makeText(ctx, "Hi MainActivity onDestroy", Toast.LENGTH_SHORT).show()
//        LogUtil.i("KW-MainActivity", "MainActivity onDestroy")
//    }
//
//    override fun onRestart() {
//        super.onRestart()
//        //Toast.makeText(ctx, "Hi MainActivity onRestart", Toast.LENGTH_SHORT).show()
//        LogUtil.i("KW-MainActivity", "MainActivity onRestart")
//    }
}