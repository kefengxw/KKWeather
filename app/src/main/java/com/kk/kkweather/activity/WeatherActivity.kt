package com.kk.kkweather.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kk.kkweather.R

/**
 * Created by xxnfd on 25/03/2018.
 */
class WeatherActivity : AppCompatActivity() {
    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_main)
    }

    companion object {
        val weatherAddrHead = "http://guolin.tech/api/weather/?cityid="
        val authKey = "9ff41582de514a658ac5f523363a6d08"
        var weatherActivityInstance : WeatherActivity? = null

        fun actionStart(context : Context, data_Country : String, data_WeatherId : String){
            val intent : Intent = Intent(context, WeatherActivity::class.java)
            intent.putExtra("weatherId", data_WeatherId)
            intent.putExtra("country", data_Country)
            context.startActivity(intent)
        }
    }
}