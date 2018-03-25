package com.kk.kkweather.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.kk.kkweather.R
import com.kk.kkweather.R.id.toolBar
import com.kk.kkweather.gson.HeWeatherItem
import com.kk.kkweather.gson.JsonWeather
import com.kk.kkweather.util.HttpUtil
import com.kk.kkweather.util.LogUtil
import kotlinx.android.synthetic.main.weather_main.*
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

/**
 * Created by xxnfd on 25/03/2018.
 */
class WeatherActivity : AppCompatActivity() {
    val context = this
    val weatherAddrHead = "http://guolin.tech/api/weather/?cityid="
    val authKey = "9ff41582de514a658ac5f523363a6d08"
    var picbgAddr : String = ""
    var picbgReady : Boolean = false
    var weatherInfoReady : Boolean = false
    var usingDefaultWeatherInfo : Boolean = false
    lateinit var jsonWeatherData : JsonWeather

    companion object {
        fun actionStart(context : Context, data_Country : String, data_WeatherId : String){
            val intent : Intent = Intent(context, WeatherActivity::class.java)
            intent.putExtra("weatherId", data_WeatherId)
            intent.putExtra("country", data_Country)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_main)

        toolBar.setTitle("My Title");
        toolBar.setSubtitle("Sub title")
        setSupportActionBar(toolBar)

        LogUtil.i("WeatherActivity", "onCreate: ${context}")

        val currentCountry : String = intent.getStringExtra("country")
        val currentWeatherId : String = intent.getStringExtra("weatherId")
        val weatherAddr = weatherAddrHead + "${currentWeatherId}&key=" + authKey

        val backgroundPicAddr : String = "http://guolin.tech/api/bing_pic"

        LogUtil.i("WeatherActivity", ("onCreate: " + weatherAddr))

        weather_cityname.text = currentCountry

        setWeatherElementVisibleByLoading(true, usingDefaultWeatherInfo)


        //*********************注意这里必须考虑先后顺序的问题*********************
        queryWeatherInfo(weatherAddr)
        queryBackgroundPic(backgroundPicAddr)
    }

    private fun queryBackgroundPic(address : String){
        HttpUtil.sendOkHttpRequest(address, object: okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //在这里进行解码Json失败的操作，当前属于子线程
                LogUtil.i("WeatherActivity", "BackgroundPic-onFailure")
                Toast.makeText(context, "Kweather failed to get BackgroundPic information", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call?, response: Response?) {
                //在这里进行解码Json操作,在这里可以看到解析好的数据，当前属于子线程
                var responseDate : String? = response?.body()?.string()

                LogUtil.i("WeatherActivity", "BackgroundPic-onResponse: ${responseDate}")

                picbgAddr = responseDate!!
                picbgReady = true

                tryToUpdateWeatherActivityUi()
            }
        })
    }

    private fun queryWeatherInfo(address : String){
        HttpUtil.sendOkHttpRequest(address, object: okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //在这里进行解码Json失败的操作，当前属于子线程
                LogUtil.i("WeatherActivity", "WeatherInfo-onFailure")
                Toast.makeText(context, "Kweather failed to get WeatherInfo information", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call?, response: Response?) {
                //在这里进行解码Json操作,在这里可以看到解析好的数据，当前属于子线程
                var responseDate : String? = response?.body()?.string()

                LogUtil.i("WeatherActivity", "WeatherInfo-onResponse: ${responseDate}")

                parseJsonWithGSONforWeather(responseDate)
            }
        })
    }

    private fun parseJsonWithGSONforWeather(jsonData:String?) {

        weatherInfoReady = true

        LogUtil.i("WeatherActivity", "Start to praseJsonWithGSON for weather!")

        if (null == jsonData || (jsonData?.contains("error")!!)) {

            usingDefaultWeatherInfo = true
            tryToUpdateWeatherActivityUi()//那就还是刷新一下吧
            return
        }

        val gson : Gson = Gson()

        jsonWeatherData = gson.fromJson(jsonData, JsonWeather::class.java)

        tryToUpdateWeatherActivityUi()
    }

    private fun tryToUpdateWeatherActivityUi() {

        if (true == weatherInfoReady && true == picbgReady)
        {
            runOnUiThread(object :Runnable {
                override fun run() {
                    //backgroundPic.setImageResource()
                    if ("" != picbgAddr) {
                        Glide.with(context).load(picbgAddr).into(backgroundPic)
                    }

                    if (false == usingDefaultWeatherInfo) {
                        UpdateAllWeatherInfo(jsonWeatherData)
                    }

                    setWeatherElementVisibleByLoading(false, usingDefaultWeatherInfo)
                }
            })
        }
        else
        {
            LogUtil.i("WeatherActivity", "天气或背景更新失败!w ${weatherInfoReady},b ${picbgReady}")
        }
    }

    private fun UpdateAllWeatherInfo(jsonWeatherData : JsonWeather) {
        LogUtil.i("WeatherActivity", "Start to UpdateAllWeatherInfo for weather!")

        for (i : HeWeatherItem in jsonWeatherData.heWeather!!)
        {
            if (weather_cityname.text != i.basic.location) {
                LogUtil.i("WeatherActivity", "Wrong city name!")
                weather_cityname.text != i.basic.location
            }

            weather_update_time.text = i.basic.update.loc
            weather_degree_now.text = i.now.tmp + "℃"
            weather_weather_now.text = i.now.condTxt
            forecast.text = "未来3天天气预报"
            forecast_day1_date.text = i.dailyForecast?.get(0)?.date
            forecast_day1_weather.text = i.dailyForecast?.get(0)?.cond?.txtD
            forecast_day1_hightemp.text = i.dailyForecast?.get(0)?.tmp?.max
            forecast_day1_lowtemp.text = i.dailyForecast?.get(0)?.tmp?.min

            forecast_day2_date.text = i.dailyForecast?.get(1)?.date
            forecast_day2_weather.text = i.dailyForecast?.get(1)?.cond?.txtD
            forecast_day2_hightemp.text = i.dailyForecast?.get(1)?.tmp?.max
            forecast_day2_lowtemp.text = i.dailyForecast?.get(1)?.tmp?.min

            forecast_day3_date.text = i.dailyForecast?.get(2)?.date
            forecast_day3_weather.text = i.dailyForecast?.get(2)?.cond?.txtD
            forecast_day3_hightemp.text = i.dailyForecast?.get(2)?.tmp?.max
            forecast_day3_lowtemp.text = i.dailyForecast?.get(2)?.tmp?.min

            aqi_index_pm25_value.text = i.aqi.city.pm
            aqi_index_air_value.text = i.aqi.city.qlty
            life_suggestions_content_comf.text = "舒适度: " + i.suggestion.comf.txt
            life_suggestions_content_sport.text = "运动指数: " + i.suggestion.sport.txt
            life_suggestions_content_car.text = "洗车建议: " + i.suggestion.cw.txt
        }

        return
    }

    fun setWeatherElementVisibleByLoading(loadingFlag: Boolean, usingDefaultWeatherInfo : Boolean){
//getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//toolBar.visibility = View.VISIBLE

        toolBar.bringToFront()
        if (true == usingDefaultWeatherInfo) {
            //显示数据加载失败
            failInfo.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
            progressBarText.visibility = View.INVISIBLE

            return
        }

        if (loadingFlag == true){
            scrollView.visibility = View.INVISIBLE
            weather_weather_now.visibility = View.INVISIBLE
            weather_degree_now.visibility = View.INVISIBLE
            weather_update_time.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            progressBarText.visibility = View.VISIBLE
        }
        else
        {
            scrollView.visibility = View.VISIBLE
            weather_weather_now.visibility = View.VISIBLE
            weather_degree_now.visibility = View.VISIBLE
            weather_update_time.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
            progressBarText.visibility = View.INVISIBLE
        }

        failInfo.visibility = View.INVISIBLE

        return
    }
}