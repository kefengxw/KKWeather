package com.kk.kkweather.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.kk.kkweather.R
import com.kk.kkweather.gson.HeWeatherItem
import com.kk.kkweather.gson.JsonWeather
import com.kk.kkweather.service.AutoUpdateWeatherService.Companion.startBackgoundUpdateWeatherInfo
import com.kk.kkweather.util.HttpUtil
import com.kk.kkweather.util.LogUtil
import kotlinx.android.synthetic.main.aqi_item.view.*
import kotlinx.android.synthetic.main.forecast_item.view.*
import kotlinx.android.synthetic.main.life_suggestion.view.*
import kotlinx.android.synthetic.main.loading_weather.view.*
import kotlinx.android.synthetic.main.title_weather.view.*
import kotlinx.android.synthetic.main.weather_main.*
import kotlinx.android.synthetic.main.wmdrawerlayout.*
import okhttp3.Call
import okhttp3.Response
import org.litepal.LitePal
import java.io.IOException

/**
 * Created by xxnfd on 25/03/2018.
 */
class WeatherActivity : AppCompatActivity() {

    val ctx = this
    val weatherAddrHead = "http://guolin.tech/api/weather/?cityid="
    val authKey = "9ff41582de514a658ac5f523363a6d08"

    var picbgReady: Boolean = false //means got the OkHttpRequest result, no matter failure or success
    var picbgAddr: String = ""      //means got the successful result
    var usingDefaultPicBgInfo: Boolean = false

    var weatherInfoReady: Boolean = false
    var weatherAddr: String = ""
    var usingDefaultWeatherInfo: Boolean = false

    lateinit var jsonWeatherData: JsonWeather
    lateinit var currentCountry: String
    lateinit var currentWeatherId: String

    companion object {
        fun actionStartWA(ctx: Context?, data_Country: String, data_WeatherId: String) {
            val intent: Intent = Intent(ctx, WeatherActivity::class.java)
            intent.putExtra("weatherId", data_WeatherId)
            intent.putExtra("country", data_Country)
            ctx?.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wmdrawerlayout)

        toolBar.setTitle("KKWeather");
        //toolBar.setSubtitle("Sub title")
        setSupportActionBar(toolBar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home_white_48dp)

        LogUtil.i("WeatherActivity", "onCreate: ${ctx}")

        currentCountry = intent.getStringExtra("country")
        currentWeatherId = intent.getStringExtra("weatherId")
        weatherAddr = weatherAddrHead + "${currentWeatherId}&key=" + authKey

        val backgroundPicAddr: String = "http://guolin.tech/api/bing_pic"

        LogUtil.i("WeatherActivity", ("onCreate: " + weatherAddr))

        title_weather_w.weather_cityname.text = currentCountry

        setWeatherElementVisibleByLoading(true, usingDefaultWeatherInfo)

        //*********************注意这里必须考虑先后顺序的问题*********************
        queryWeatherInfo(weatherAddr)
        queryBackgroundPic(backgroundPicAddr)

        setCityHomeButtionListener(ctx)
        //switch_debug.setOnCheckedChangeListener(this)
        setSwipeRefreshListener()

        startAutoUpdateWeatherService()
    }

    private fun startAutoUpdateWeatherService() {
        LogUtil.i("WeatherActivity", "startAutoUpdateWeatherService")

        startBackgoundUpdateWeatherInfo(ctx, currentCountry, currentWeatherId, weatherAddr)
    }

    private fun setSwipeRefreshListener() {
        swipe_refresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                //Toast.makeText(ctx, "Hi SwipeRefreshLayout", Toast.LENGTH_SHORT).show()
                refreshWeatherManually()
            }
        })
    }

    private fun refreshWeatherManually() {
        usingDefaultWeatherInfo = false
        setWeatherElementVisibleByLoading(true, usingDefaultWeatherInfo)

        weatherInfoReady = false
        queryWeatherInfo(weatherAddr, false)
    }

    private fun queryBackgroundPic(address: String) {
        HttpUtil.sendOkHttpRequest(address, object : okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //在这里进行解码Json失败的操作，当前属于子线程
                LogUtil.i("WeatherActivity", "BackgroundPic-onFailure")
                usingDefaultPicBgInfo = true
                picbgReady = true
                //201需要处理一下
            }

            override fun onResponse(call: Call?, response: Response?) {
                //在这里进行解码Json操作,在这里可以看到解析好的数据，当前属于子线程
                var responseDate: String? = response?.body()?.string()

                LogUtil.i("WeatherActivity", "BackgroundPic-onResponse: ${responseDate}")

                picbgAddr = responseDate!!
                picbgReady = true

                tryToUpdateWeatherActivityUi()
            }
        })
    }

    private fun queryWeatherInfo(address: String, localDataflag: Boolean = true) {

        if (true == localDataflag) {
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
            val jsonData = prefs.getString("weatherInfo", null)
            val wiid: String? = prefs.getString("weatherInfoId", null)
            val wid: String? = prefs.getString("weatherId", null)
            if (null != wiid && null != wid && wiid.equals(wid) && (jsonData != null)) {//it does not seems make sense, still not so quick
                weatherInfoReady = true
                parseJsonWithGSONforWeather(jsonData)
                return
            }
        }

        HttpUtil.sendOkHttpRequest(address, object : okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //在这里进行解码Json失败的操作，当前属于子线程
                LogUtil.i("WeatherActivity", "WeatherInfo-onFailure")
                weatherInfoReady = true
                usingDefaultWeatherInfo = true
                tryToUpdateWeatherActivityUi()
            }

            override fun onResponse(call: Call?, response: Response?) {
                //在这里进行解码Json操作,在这里可以看到解析好的数据，当前属于子线程
                var responseDate: String? = response?.body()?.string()

                LogUtil.i("WeatherActivity", "WeatherInfo-onResponse: ${responseDate}")

                weatherInfoReady = true
                parseJsonWithGSONforWeather(responseDate)
            }
        })
    }

    private fun parseJsonWithGSONforWeather(jsonData: String?) {

        LogUtil.i("WeatherActivity", "Start to praseJsonWithGSON for weather!")

        if (null == jsonData || (jsonData.contains("error"))) {
            usingDefaultWeatherInfo = true
            tryToUpdateWeatherActivityUi()//那就还是刷新一下吧
            return
        }

        val gson: Gson = Gson()

        jsonWeatherData = gson.fromJson(jsonData, JsonWeather::class.java)

        tryToUpdateWeatherActivityUi(jsonData)
    }

    private fun tryToUpdateWeatherActivityUi(jsonData: String? = null) {

        if (true == weatherInfoReady && true == picbgReady) {
            runOnUiThread(object : Runnable {
                override fun run() {
                    //backgroundPic.setImageResource()
                    if ("" != picbgAddr) {
                        Glide.with(ctx).load(picbgAddr).into(backgroundPic)
                    } else {
                        //使用本地的pic,后续补充101
                        //Glide.with(ctx).load(picbgAddr).into(backgroundPic)
                    }
                    if (false == usingDefaultWeatherInfo) UpdateAllWeatherInfo(jsonWeatherData)

                    setWeatherElementVisibleByLoading(false, usingDefaultWeatherInfo)

                    // if refresh success, than save local data
                    var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
                    var editor: SharedPreferences.Editor = prefs.edit()
                    editor.putString("weatherInfoId", currentWeatherId)
                    editor.putString("weatherInfo", jsonData)
                    editor.apply()
                }
            })
        } else {
            LogUtil.i("WeatherActivity", "天气或背景更新失败!w ${weatherInfoReady},b ${picbgReady}")
        }

        swipe_refresh.setRefreshing(false)
    }

    private fun UpdateAllWeatherInfo(jsonWeatherData: JsonWeather) {
        LogUtil.i("WeatherActivity", "Start to UpdateAllWeatherInfo for weather!")

        for (i: HeWeatherItem in jsonWeatherData.heWeather!!) {
            if (title_weather_w.weather_cityname.text != i.basic.location) {
                LogUtil.i("WeatherActivity", "Wrong city name!")
                title_weather_w.weather_cityname.text != i.basic.location
            }

            title_weather_w.weather_update_time.text = i.basic.update.loc
            title_weather_w.weather_degree_now.text = i.now.tmp + "℃"
            title_weather_w.weather_weather_now.text = i.now.condTxt
            //forecast.text = "Forecast in Next 3 Days"

            forecast_day1.forecast_date.text = i.dailyForecast?.get(0)?.date
            forecast_day1.forecast_weather.text = i.dailyForecast?.get(0)?.cond?.txtD
            forecast_day1.forecast_hightemp.text = i.dailyForecast?.get(0)?.tmp?.max
            forecast_day1.forecast_lowtemp.text = i.dailyForecast?.get(0)?.tmp?.min

            forecast_day2.forecast_date.text = i.dailyForecast?.get(1)?.date
            forecast_day2.forecast_weather.text = i.dailyForecast?.get(1)?.cond?.txtD
            forecast_day2.forecast_hightemp.text = i.dailyForecast?.get(1)?.tmp?.max
            forecast_day2.forecast_lowtemp.text = i.dailyForecast?.get(1)?.tmp?.min

            forecast_day3.forecast_date.text = i.dailyForecast?.get(2)?.date
            forecast_day3.forecast_weather.text = i.dailyForecast?.get(2)?.cond?.txtD
            forecast_day3.forecast_hightemp.text = i.dailyForecast?.get(2)?.tmp?.max
            forecast_day3.forecast_lowtemp.text = i.dailyForecast?.get(2)?.tmp?.min

            aqi_index_value.aqi_index_pm25.text = i.aqi.city.pm
            aqi_index_value.aqi_index_air.text = i.aqi.city.pm

            life_suggestion_w.life_suggestions_content_comf.text = "舒适度: " + i.suggestion.comf.txt
            life_suggestion_w.life_suggestions_content_sport.text = "运动指数: " + i.suggestion.sport.txt
            life_suggestion_w.life_suggestions_content_car.text = "洗车建议: " + i.suggestion.cw.txt
        }

        return
    }

    fun setWeatherElementVisibleByLoading(loadingFlag: Boolean, usingDefaultWeatherInfo: Boolean) {
//getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//toolBar.visibility = View.VISIBLE

        toolBar.bringToFront()
        if (true == usingDefaultWeatherInfo) {
            //显示数据加载失败
            loading_component.failInfo.visibility = View.VISIBLE
            loading_component.progressBarText.visibility = View.INVISIBLE
            if (false == swipe_refresh.isRefreshing())
                loading_component.progressBar.visibility = View.INVISIBLE

            return
        }

        if (loadingFlag == true) {
            scrollView.visibility = View.INVISIBLE
            title_weather_w.weather_weather_now.visibility = View.INVISIBLE
            title_weather_w.weather_degree_now.visibility = View.INVISIBLE
            title_weather_w.weather_update_time.visibility = View.INVISIBLE
            loading_component.progressBarText.visibility = View.VISIBLE
            if (false == swipe_refresh.isRefreshing())
                loading_component.progressBar.visibility = View.VISIBLE
        } else {
            scrollView.visibility = View.VISIBLE
            title_weather_w.weather_weather_now.visibility = View.VISIBLE
            title_weather_w.weather_degree_now.visibility = View.VISIBLE
            title_weather_w.weather_update_time.visibility = View.VISIBLE
            loading_component.progressBarText.visibility = View.INVISIBLE
            if (false == swipe_refresh.isRefreshing())
                loading_component.progressBar.visibility = View.INVISIBLE
        }

        loading_component.failInfo.visibility = View.INVISIBLE

        return
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbaritem, menu)
        //Toast.makeText(ctx, "Hi onCreateOptionsMenu", Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.app_bar_shot -> {
                Toast.makeText(ctx, "Hi app_bar_shot", Toast.LENGTH_SHORT).show()
            }
            R.id.app_bar_setting -> {
                Toast.makeText(ctx, "Hi app_bar_setting", Toast.LENGTH_SHORT).show()
                //不响应任何事件处理
            }
            R.id.app_bar_refresh -> {
                //Toast.makeText(ctx, "Hi app_bar_refresh", Toast.LENGTH_SHORT).show()
                refreshWeatherManually()
            }
            android.R.id.home -> {//一定需要加android,不然无效，这个不是资源，而是android系统自定义的值
                //Toast.makeText(ctx, "Hi home AS UP", Toast.LENGTH_SHORT).show()
                weather_main_drawer_layout.openDrawer(GravityCompat.START)
                //val v: View = LayoutInflater.from(ctx).inflate(R.layout.activity_main, weather_main, false) 不行
                //drawer_layout.closeDrawers()
            }
            else -> {//R.id.app_bar_refresh
                Toast.makeText(ctx, "Hi other", Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }

//    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//        Toast.makeText(ctx, "Hi switch", Toast.LENGTH_SHORT).show()
//    }

    private fun setCityHomeButtionListener(ctx: Context?) {
//        title_weather_w.weather_cityhome.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                LitePal.getDatabase()
//            }
//        })
    }

    fun closeDrawers() {
        weather_main_drawer_layout.closeDrawers()
    }
}