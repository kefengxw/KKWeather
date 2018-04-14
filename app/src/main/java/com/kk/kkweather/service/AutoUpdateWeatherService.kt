package com.kk.kkweather.service

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import android.os.SystemClock
import android.preference.PreferenceManager
import com.kk.kkweather.util.HttpUtil
import com.kk.kkweather.util.LogUtil
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class AutoUpdateWeatherService : IntentService("KKWeather") {

    lateinit var currentCountry :String
    lateinit var currentWeatherId : String
    lateinit var cweatherAddr : String
    lateinit var manager : AlarmManager

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            "AutoUpdateWeather" -> {
                currentCountry = intent.getStringExtra("currentCountry")
                currentWeatherId = intent.getStringExtra("currentWeatherId")
                cweatherAddr = intent.getStringExtra("weatherAddr")
                val flag = intent.getStringExtra("flag")
                if (flag.equals("true"))
                    handleBackgoundUpdateWeatherInfo(cweatherAddr)//no need to update first time
            }
            //"AutoUpdateBgPic" ->
        }
        //use the alarm, and try to recovery backgound task again
        val i = Intent(this, AutoUpdateWeatherService::class.java)

        i.action = "AutoUpdateWeather"
        i.putExtra("currentCountry", currentCountry)
        i.putExtra("currentWeatherId", currentWeatherId)
        i.putExtra("weatherAddr", cweatherAddr)
        i.putExtra("flag", "true")

        manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val periodTime : Int = (8)*(60)*60*1000  //8 hours
        val triggerAtTime = SystemClock.elapsedRealtime() + periodTime
        val pi = PendingIntent.getService(this,0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        manager.cancel(pi)
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi)
        //manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

    private fun handleBackgoundUpdateWeatherInfo(cweatherAddr: String) {
        backgroundQueryWeatherInfo(cweatherAddr)
    }

    private fun backgroundQueryWeatherInfo(address : String){
        HttpUtil.sendOkHttpRequest(address, object: okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                LogUtil.i("AutoUpdateWeatherService", "WeatherInfo-onFailure")
            }

            override fun onResponse(call: Call?, response: Response?) {
                var responseDate : String? = response?.body()?.string()
                LogUtil.i("AutoUpdateWeatherService", "WeatherInfo-onResponse: ${responseDate}")

                if (null == responseDate || (responseDate.contains("error"))) {
                    return
                }

                var prefs : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@AutoUpdateWeatherService)
                var editor : SharedPreferences.Editor = prefs.edit()
                editor.putString("weatherInfoId", currentWeatherId)
                editor.putString("weatherInfo", responseDate)
                editor.apply()
            }
        })
    }

    companion object {
        fun startBackgoundUpdateWeatherInfo(context: Context, currentCountry: String, currentWeatherId: String, weatherAddr: String) {
            val intent = Intent(context, AutoUpdateWeatherService::class.java).apply {
                action = "AutoUpdateWeather"
                putExtra("currentCountry", currentCountry)
                putExtra("currentWeatherId", currentWeatherId)
                putExtra("weatherAddr", weatherAddr)
                putExtra("flag", "false")
            }
            context.startService(intent)
        }
    }
}