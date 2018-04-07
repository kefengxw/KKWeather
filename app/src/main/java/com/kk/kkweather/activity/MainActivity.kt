package com.kk.kkweather.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.kk.kkweather.util.LogUtil
import android.widget.Toast
import com.kk.kkweather.R


class MainActivity : AppCompatActivity() {

    var ctx = this
    //var ctxtest = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (true == tryToStartWeatherActivity())
        {
            LogUtil.i("MainActivity", "Successful to start WeatherActivity")
            return
        }

        setContentView(R.layout.amframelayout)
    }

    private fun tryToStartWeatherActivity() : Boolean{
        //后续可以根据定位来确定，或者根据设置来确定城市
        var prefs : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        var i = prefs.getString("weatherId", null)
        var j = prefs.getString("country", null)
        var ret = false
        if (i != null)
        {
            WeatherActivity.actionStart(ctx, j, i)
            this.finish()
            ret = true
        }
        return ret
    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(ctx, "Hi MainActivity onStart", Toast.LENGTH_SHORT).show()
        LogUtil.i("KW-MainActivity", "MainActivity onStart")
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(ctx, "Hi MainActivity onResume", Toast.LENGTH_SHORT).show()
        LogUtil.i("KW-MainActivity", "MainActivity onResume")
    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(ctx, "Hi MainActivity onPause", Toast.LENGTH_SHORT).show()
        LogUtil.i("KW-MainActivity", "MainActivity onPause")
    }

    override fun onStop() {
        super.onStop()
        Toast.makeText(ctx, "Hi MainActivity onStop", Toast.LENGTH_SHORT).show()
        LogUtil.i("KW-MainActivity", "MainActivity onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(ctx, "Hi MainActivity onDestroy", Toast.LENGTH_SHORT).show()
        LogUtil.i("KW-MainActivity", "MainActivity onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Toast.makeText(ctx, "Hi MainActivity onRestart", Toast.LENGTH_SHORT).show()
        LogUtil.i("KW-MainActivity", "MainActivity onRestart")
    }
}