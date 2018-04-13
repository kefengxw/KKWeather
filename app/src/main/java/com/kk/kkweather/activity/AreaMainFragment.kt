package com.kk.kkweather.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kk.kkweather.R
import com.kk.kkweather.db.dbCity
import com.kk.kkweather.db.dbCounty
import com.kk.kkweather.db.dbProvince
import com.kk.kkweather.gson.JsonCity
import com.kk.kkweather.gson.JsonCountry
import com.kk.kkweather.gson.JsonProvince
import com.kk.kkweather.util.HttpUtil
import com.kk.kkweather.util.LogUtil
import kotlinx.android.synthetic.main.area_main_fragment.view.*
import okhttp3.Call
import okhttp3.Response
import org.litepal.crud.DataSupport
import java.io.IOException

/**
 * Created by xxnfd on 28/03/2018.
 */

class AreaMainFragment : Fragment() {

    var ctx: Context? = null
    lateinit var areaListView: RecyclerView
    lateinit var currentView: View
    lateinit var areaAdapter: AreaRecyListAdapter

    var currentLevel: Int = LEVEL_PROVINCE

    var selectProvince: String = ""
    var selectProvinceId: Int = 0
    var selectCity: String = ""
    var selectCityId: Int = 0

    val URL_OF_AREA: String = "http://guolin.tech/api/china"

    companion object {
        val LEVEL_PROVINCE: Int = 1
        val LEVEL_CITY: Int = 2
        val LEVEL_COUNTRY: Int = 3
        val LEVEL_WEATHER: Int = 4
        var areaList: MutableList<AreaItem> = mutableListOf()
        var activityType: Int = 0//Default 0 (AreaMainAcitivity), 1 (WeatherAcitivity)
        val ACTIVITY_TYPE_AREA_MAIN_ACTIVITY: Int = 0
        val ACTIVITY_TYPE_WEATHER_DRAWER_ACTIVITY: Int = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //must update ctx here, because this is only Fragment
        ctx = context

        val view = inflater.inflate(R.layout.area_main_fragment, container, false)
        currentView = view

        LogUtil.i("AreaMainFragment", "onCreateView")

        //Toast.makeText(ctx, "Hi MainActivity onCreate", Toast.LENGTH_SHORT).show()
        //LocalDateInitProvince()
        //LocalDateInitCity()
        //LocalDateInitCounty()

        areaAdapter = AreaRecyListAdapter(areaList, ctx)

        val linearLayoutManager = LinearLayoutManager(ctx)
        //linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        //areaList.layoutManager = GridLayoutManager(this,3)
        areaListView = currentView.area_main_recylistview
        areaListView.layoutManager = linearLayoutManager//LinearLayoutManager(this)
        areaListView.adapter = areaAdapter

        switchToProvinceActivity()//call this funtion first, while else can not areaList
        setBackButtionListener()
        setAreaAdapterListener()

        return view
    }

    private fun setBackButtionListener() {
        currentView.area_main_title_backbutton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                when (currentLevel) {
                    LEVEL_PROVINCE -> {
                        //do nothing
                    }
                    LEVEL_CITY -> {
                        backToProvinceActivity()
                    }
                    LEVEL_COUNTRY -> {
                        backToCityActivity()
                    }
                }
            }
        })
    }

    private fun setAreaAdapterListener() {
        areaAdapter.setOnAreaItemClickListener(object : OnAreaItemClickListener {
            override fun onAreaItemClick(view: View, position: Int) {
                when (currentLevel) {
                    LEVEL_PROVINCE -> {
                        //Toast.makeText(ctx, "Hi Click Province - ${areaList[position].name}, Position:${position}", Toast.LENGTH_SHORT).show()
                        selectProvince = areaList[position].name
                        selectProvinceId = areaList[position].id

                        switchToCityActivity(areaList[position].name, areaList[position].id)
                    }
                    LEVEL_CITY -> {
                        //Toast.makeText(ctx, "Hi Click City- ${areaList[position].name}", Toast.LENGTH_SHORT).show()
                        selectCity = areaList[position].name
                        selectCityId = areaList[position].id

                        switchToCountyActivity(areaList[position].name, selectProvinceId, areaList[position].id)
                    }
                    LEVEL_COUNTRY -> {
                        switchToWeatherActivity(ctx, areaList[position].name, areaList[position].weatherId)
                    }
                }
            }
        })
    }

    private fun switchToProvinceActivity() {
        currentLevel = LEVEL_PROVINCE

        queryProvinceInfo()
        areaListView.setHasFixedSize(true)
        currentView.area_main_title_backbutton.visibility = View.INVISIBLE
        currentView.area_main_title_text.text = "China"
    }

    private fun switchToCityActivity(province: String, provinceId: Int) {
        currentLevel = LEVEL_CITY

        queryCityInfo(provinceId)
        currentView.area_main_title_backbutton.visibility = View.VISIBLE
        currentView.area_main_title_text.text = province
    }

    private fun switchToCountyActivity(city: String, provinceId: Int, cityId: Int) {
        currentLevel = LEVEL_COUNTRY

        queryCountyInfo(provinceId, cityId)
        currentView.area_main_title_backbutton.visibility = View.VISIBLE
        currentView.area_main_title_text.text = city
    }

    private fun switchToWeatherActivity(ctx: Context?, country: String, weatherId: String) {
        //save local data without condition
        var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        var editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("country", country)
        editor.putString("weatherId", weatherId)
        editor.apply()

        WeatherActivity.actionStartWA(ctx, country, weatherId)

        if (AreaMainFragment.ACTIVITY_TYPE_AREA_MAIN_ACTIVITY != AreaMainFragment.activityType) {
            val acti: WeatherActivity = getActivity() as WeatherActivity
            acti.closeDrawers()
        }

        getActivity()?.finish()
    }

    private fun backToCityActivity() {
        currentLevel = LEVEL_CITY
        currentView.area_main_title_backbutton.visibility = View.VISIBLE
        currentView.area_main_title_text.text = selectProvince

        queryAreaInfofromDatabaseAndRefreshDataOnUI(selectProvinceId)
    }

    private fun backToProvinceActivity() {
        currentLevel = LEVEL_PROVINCE
        currentView.area_main_title_backbutton.visibility = View.INVISIBLE
        currentView.area_main_title_text.text = "China"

        queryAreaInfofromDatabaseAndRefreshDataOnUI(0)
    }

    private fun queryProvinceInfo() {
        if (0 == DataSupport.count(dbProvince::class.java)) {
            val address: String = URL_OF_AREA
            queryAreaInfofromServer(address)
        } else {
            queryAreaInfofromDatabaseAndRefreshDataOnUI(0)
        }
    }

    private fun queryCityInfo(provinceId: Int) {
        val dbcilist: List<dbCity> = DataSupport.where("provinceCode=?", "${provinceId}").find(dbCity::class.java)
        if (0 == dbcilist.size) {
            val address: String = URL_OF_AREA + "/${provinceId}"
            queryAreaInfofromServer(address)
        } else {
            queryAreaInfofromDatabaseAndRefreshDataOnUI(provinceId)
        }
    }

    private fun queryCountyInfo(provinceId: Int, cityId: Int) {
        val dbcilist: List<dbCounty> = DataSupport.where("cityCode=?", "${cityId}").find(dbCounty::class.java)
        if (0 == dbcilist.size) {
            val address: String = URL_OF_AREA + "/${provinceId}/${cityId}"
            queryAreaInfofromServer(address)
        } else {
            queryAreaInfofromDatabaseAndRefreshDataOnUI(cityId)
        }
    }

    private fun queryAreaInfofromDatabaseAndRefreshDataOnUI(inputId: Int) {
        queryAreaInfofromDatabase(inputId)
        refreshAreaListDataOnUI()
    }

    private fun queryAreaInfofromDatabase(inputId: Int) {
        when (currentLevel) {
            LEVEL_PROVINCE -> {
                val dbplist: List<dbProvince> = DataSupport.where("provinceCode>?", "0")
                        .order("provinceCode")//need to sort after get all the data
                        .find(dbProvince::class.java)
                areaList.clear()
                for (i in dbplist) areaList.add(AreaItem(name = i.provinceName, id = i.provinceCode))
            }

            LEVEL_CITY -> {
                val dbcilist: List<dbCity> = DataSupport.where("provinceCode=?", "${inputId}")
                        .order("cityCode")//need to sort after get all the data
                        .find(dbCity::class.java)
                areaList.clear()
                for (i in dbcilist) areaList.add(AreaItem(name = i.cityName, id = i.cityCode))
            }

            LEVEL_COUNTRY -> {
                val dbcolist: List<dbCounty> = DataSupport.where("cityCode=?", "${inputId}")
                        .order("countryCode")//need to sort after get all the data
                        .find(dbCounty::class.java)
                areaList.clear()
                for (i in dbcolist) areaList.add(AreaItem(name = i.countyName, id = i.countryCode, weatherId = i.weatherCode))
            }
        }
    }

    private fun queryAreaInfofromServer(address: String) {
        HttpUtil.sendOkHttpRequest(address, object : okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //decode Json failed, it belongs to sub thread
                LogUtil.i("HttpCallback", "queryAreaInfo-onFailure")
                //Toast.makeText(ctx, "AreaMainFragment failed to get Area information", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call?, response: Response?) {
                //decode Json successful, it belongs to sub thread
                var responseDate: String? = response?.body()?.string()
                LogUtil.i("HttpCallback", "queryAreaInfo-onResponse: ${responseDate}")
                parseJsonWithGSONforDb(responseDate)
            }
        })
    }

    private fun parseJsonWithGSONforDb(jsonData: String?) {
        //the data from server, make sure only write database, to make sure the only source for database and data struct
        //Data flow: Internet-->Database-->DataStruct, if bug happens, easy to find it

        LogUtil.i("HttpCallback", "Start to praseJsonWithGSON for AreaInfo")

        if (null == jsonData) return

        val gson: Gson = Gson()
        var inputId: Int = 0 //province level as default

        when (currentLevel) {
            LEVEL_PROVINCE -> {
                val myType = object : TypeToken<MutableList<JsonProvince>>() {}.type
                val numbers: MutableList<JsonProvince> = gson.fromJson(jsonData, myType)

                for (i: JsonProvince in numbers) {
                    LogUtil.i("HttpCallback", "praseJsonWithGSONfor ${currentLevel}-->${i.id} + ${i.name}")
                    //only decode and update data here, better not to do control things
                    val dbplist: List<dbProvince> = DataSupport.where("provinceCode=?", "${i.id}").find(dbProvince::class.java)
                    val dbp = dbProvince(provinceCode = i.id, provinceName = i.name)
                    //for(x in ii){ LogUtil.i("xxx","yyy") }
                    if (0 == dbplist.size) {
                        dbp.save()
                    } else {
                        dbp.updateAll("provinceCode=?", "${i.id}")
                    }//need to delete data on the database, if area changes
                }
            }

            LEVEL_CITY -> {
                val myType = object : TypeToken<MutableList<JsonCity>>() {}.type
                val numbers: MutableList<JsonCity> = gson.fromJson(jsonData, myType)

                for (i: JsonCity in numbers) {
                    LogUtil.i("HttpCallback", "praseJsonWithGSONfor ${currentLevel}-->${i.id} + ${i.name}")

                    val dbcilist: List<dbCity> = DataSupport.where("cityCode=?", "${i.id}").find(dbCity::class.java)
                    val dbci = dbCity(cityCode = i.id, cityName = i.name, provinceCode = selectProvinceId)
                    if (0 == dbcilist.size) {
                        dbci.save()
                    } else {
                        dbci.updateAll("cityCode=?", "${i.id}")
                    }//need to delete data on the database, if area changes
                }
                inputId = selectProvinceId
            }

            LEVEL_COUNTRY -> {
                val myType = object : TypeToken<MutableList<JsonCountry>>() {}.type
                val numbers: MutableList<JsonCountry> = gson.fromJson(jsonData, myType)

                for (i: JsonCountry in numbers) {
                    LogUtil.i("HttpCallback", "praseJsonWithGSONfor ${currentLevel}-->${i.id} + ${i.name} + ${i.weather_id}")

                    val dbcolist: List<dbCounty> = DataSupport.where("countryCode=?", "${i.id}").find(dbCounty::class.java)
                    val dbco = dbCounty(countryCode = i.id, countyName = i.name, cityCode = selectCityId, weatherCode = i.weather_id)
                    if (0 == dbcolist.size) {
                        dbco.save()
                    } else {
                        dbco.updateAll("countryCode=?", "${i.id}")
                    }//need to delete data on the database, if area changes
                }
                inputId = selectCityId
            }
        }

        queryAreaInfofromDatabaseAndRefreshDataOnUIfromSubThread(inputId)
    }

//    fun getAreaListData():MutableList<AreaItem>{ return areaList }

    private fun refreshAreaListDataOnUI() {
        areaListView.adapter.notifyDataSetChanged()
        areaListView.getLayoutManager().scrollToPosition(0)
    }

    private fun queryAreaInfofromDatabaseAndRefreshDataOnUIfromSubThread(inputId: Int) {

        queryAreaInfofromDatabase(inputId)

        getActivity()?.runOnUiThread(object : Runnable {
            override fun run() {
                refreshAreaListDataOnUI()
            }
        })
    }

//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        LogUtil.i("AreaMainFragment", "onAttach")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        LogUtil.i("AreaMainFragment", "onPause")
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        LogUtil.i("AreaMainFragment", "onActivityCreated")
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        LogUtil.i("AreaMainFragment", "onCreate")
//    }
//
//    override fun onStart() {
//        super.onStart()
//        LogUtil.i("AreaMainFragment", "onStart")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        LogUtil.i("AreaMainFragment", "onResume")
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        LogUtil.i("AreaMainFragment", "onDetach")
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        LogUtil.i("AreaMainFragment", "onDestroyView")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        LogUtil.i("AreaMainFragment", "onStop")
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        LogUtil.i("AreaMainFragment", "onDestroy")
//    }
}