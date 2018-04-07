package com.kk.kkweather.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kk.kkweather.R
import com.kk.kkweather.db.dbProvince
import com.kk.kkweather.gson.JsonCity
import com.kk.kkweather.gson.JsonCountry
import com.kk.kkweather.gson.JsonProvince
import com.kk.kkweather.util.HttpUtil
import com.kk.kkweather.util.LogUtil
import kotlinx.android.synthetic.main.area_main_fragment.*
import kotlinx.android.synthetic.main.area_main_fragment.view.*
import okhttp3.Call
import okhttp3.Response
import org.litepal.crud.DataSupport
import java.io.IOException

/**
 * Created by xxnfd on 28/03/2018.
 */

class AreaMainFragment : Fragment() {
    var ctx : Context? = null
    lateinit var areaListView: RecyclerView
    lateinit var currentView : View
    lateinit var areaAdapter : AreaRecyListAdapter

    var areaListProvince : MutableList<AreaItem> = mutableListOf()
    var areaListCity : MutableList<AreaItem> = mutableListOf()
    var areaListCountry : MutableList<AreaItem> = mutableListOf()

    var currentLevel : Int = LEVEL_PROVINCE

    var selectProvince : String = "美国101"
    var selectProvinceId :Int = 0
    var selectCity : String = "加利福尼亚102"
    var selectCountry : String = "乡村1号103"

    val URL_OF_AREA : String = "http://guolin.tech/api/china"

    companion object {
        val LEVEL_PROVINCE : Int = 1
        val LEVEL_CITY : Int = 2
        val LEVEL_COUNTRY : Int = 3
        val LEVEL_WEATHER : Int = 4
        var areaList : MutableList<AreaItem> = mutableListOf()

        fun actionStart(context : Context, data_Country : String, data_WeatherId : String, flag : String){
            val intent : Intent = Intent(context, MainActivity::class.java)
            intent.putExtra("weatherId1", data_WeatherId)
            intent.putExtra("country1", data_Country)
            intent.putExtra("flag", flag)
            context.startActivity(intent)
        }
    }

    private fun LocalDateInitProvince(){
//        有几个问题待解决,1.只有少量数据的时候,底部有白色的边，2.最后一个数据为什么不显示?
        areaListProvince.add(AreaItem("01Sweden"))
        areaListProvince.add(AreaItem("02加拿大"))
        areaListProvince.add(AreaItem("03佛得角"))
        areaListProvince.add(AreaItem("04开曼群岛"))
        areaListProvince.add(AreaItem("05中非共和国"))
        areaListProvince.add(AreaItem("06乍得"))
        areaListProvince.add(AreaItem("07智利"))
        areaListProvince.add(AreaItem("08中国"))
        areaListProvince.add(AreaItem("09斐济"))
        areaListProvince.add(AreaItem("10芬兰"))
        areaListProvince.add(AreaItem("11法国"))
        areaListProvince.add(AreaItem("12法属圭亚那"))
        areaListProvince.add(AreaItem("13法属波利尼西亚"))
        areaListProvince.add(AreaItem("14加蓬"))
        areaListProvince.add(AreaItem("15冈比亚"))
        areaListProvince.add(AreaItem("16格鲁吉亚"))
        areaListProvince.add(AreaItem("17德国"))
    }

    private fun LocalDateInitCity() {
//        有几个问题待解决,1.只有少量数据的时候,底部有白色的边，2.最后一个数据为什么不显示?
        areaListCity.add(AreaItem("01南通"))
        areaListCity.add(AreaItem("02苏州"))
        areaListCity.add(AreaItem("03无锡"))
        areaListCity.add(AreaItem("04徐州"))
        areaListCity.add(AreaItem("05常州"))
        areaListCity.add(AreaItem("06镇江"))
        areaListCity.add(AreaItem("07扬州"))
        areaListCity.add(AreaItem("08连云港"))
        areaListCity.add(AreaItem("09宿迁"))
        areaListCity.add(AreaItem("10南京"))
        areaListCity.add(AreaItem("11盐城"))
        areaListCity.add(AreaItem("12Stockholm"))
    }

    private fun LocalDateInitCounty() {
//        有几个问题待解决,1.只有少量数据的时候,底部有白色的边，2.最后一个数据为什么不显示?
        areaListCountry.add(AreaItem("01海安"))
        areaListCountry.add(AreaItem("02东台"))
        areaListCountry.add(AreaItem("03如东"))
        areaListCountry.add(AreaItem("04启东"))
        areaListCountry.add(AreaItem("05闸港"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //在这里进行更新cnx，必须更新
        ctx = context

        val view = inflater.inflate(R.layout.area_main_fragment, container, false)

        currentView = view

        LogUtil.i("AreaMainFragment", "onCreateView")

        Toast.makeText(ctx, "Hi MainActivity onCreate", Toast.LENGTH_SHORT).show()

//        LocalDateInitProvince()
//        LocalDateInitCity()
//        LocalDateInitCounty()

        areaAdapter = AreaRecyListAdapter(areaList, ctx)

        val linearLayoutManager = LinearLayoutManager(ctx)
        //        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        //        areaList.layoutManager = GridLayoutManager(this,3)
        areaListView = currentView.area_main_recylistview

        areaListView.layoutManager = linearLayoutManager//LinearLayoutManager(this)

        areaListView.adapter = areaAdapter

        switchToProvinceActivity(ctx)//必须在这个之前调用，否则非法访问areaList

        setBackButtionListener(ctx)
        setAreaAdapterListener()

        return view
    }

    private fun setBackButtionListener(ctx: Context?) {
        currentView.area_main_title_backbutton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                when (currentLevel) {
                    LEVEL_PROVINCE -> {
                        //do nothing
                    }
                    LEVEL_CITY -> {
                        backToProvinceActivity(ctx)
                    }
                    LEVEL_COUNTRY -> {
                        backToCityActivity(ctx)
                    }
                }
            }
        })
    }

    private fun setAreaAdapterListener(){
        areaAdapter.setOnAreaItemClickListener(object : OnAreaItemClickListener{
            override fun onAreaItemClick(view: View, position: Int) {
                when (currentLevel){//可以优化
                    LEVEL_PROVINCE -> {
                        Toast.makeText(ctx, "Hi Click Province - ${areaList[position].name}, Position:${position}", Toast.LENGTH_SHORT).show()
                        selectProvince = areaList[position].name
                        selectProvinceId = areaList[position].id
                        switchToCityActivity(ctx, areaList[position].name, areaList[position].id)


//                        var dbp = dbProvince()
//                        dbp.id = 50
//                        dbp.provinceName = "jiangsu50"
//                        dbp.proviceCode = 50
//                        dbp.save()
//
//
//                        val ii : List<dbProvince> = DataSupport.findAll(dbProvince::class.java)
//                        for(x in ii){
//                            LogUtil.i("xxx","yyy")
//                        }

                    }
                    LEVEL_CITY -> {
                        Toast.makeText(ctx, "Hi Click City- ${areaList[position].name}", Toast.LENGTH_SHORT).show()
                        selectCity = areaList[position].name

                        switchToCountyActivity(ctx, areaList[position].name, selectProvinceId, areaList[position].id)
                    }
                    LEVEL_COUNTRY -> {
                        switchToWeatherActivity(ctx, areaList[position].name, areaList[position].weatherId)
                    }
                }
            }
        })
    }

    private fun switchToProvinceActivity(ctx: Context?)
    {
        //areaListProvince.clear()//第一次清空省份信息，只是做测试使用
        areaList.clear()
        if (areaListProvince.isEmpty()){
            queryProvinceInfo()
        }else{
            for(i in areaListProvince)  areaList.add(i)
            areaListView.adapter.notifyDataSetChanged()
        }

        areaListView.setHasFixedSize(true)
        currentLevel = LEVEL_PROVINCE
        currentView.area_main_title_backbutton.visibility = View.INVISIBLE
        currentView.area_main_title_text.text = "中国"
    }

    private fun switchToCityActivity(ctx: Context?, province: String, provinceId: Int)
    {
        areaList.clear()
        if (areaListCity.isEmpty()){
            queryCityInfo(province, provinceId)
        }else{
            for(i in areaListCity)  areaList.add(i)
            areaListView.adapter.notifyDataSetChanged()
        }

        currentLevel = LEVEL_CITY
        currentView.area_main_title_backbutton.visibility = View.VISIBLE
        currentView.area_main_title_text.text = province
    }

    private fun switchToCountyActivity(ctx: Context?, city: String, provinceId: Int, cityId: Int)
    {
        areaList.clear()
        if (areaListCountry.isEmpty()){
            queryCountyInfo(provinceId, cityId)
        }else{
            for(i in areaListCountry)  areaList.add(i)
            areaListView.adapter.notifyDataSetChanged()
        }

        currentLevel = LEVEL_COUNTRY
        currentView.area_main_title_backbutton.visibility = View.VISIBLE
        currentView.area_main_title_text.text = city
    }

    private fun switchToWeatherActivity(ctx: Context?, country: String, weatherId: String)
    {
        var prefs : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        WeatherActivity.actionStart(ctx, country, weatherId)
        //保存到本地
        var editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("country", country)
        editor.putString("weatherId", weatherId)
        editor.apply()

        getActivity()?.finish()
    }

    private fun backToCityActivity(ctx: Context?)
    {
        areaList.clear()
        areaListCountry.clear()
        for(i in areaListCity)  areaList.add(i)
        areaListView.adapter.notifyDataSetChanged()

        currentLevel = LEVEL_CITY
        currentView.area_main_title_backbutton.visibility = View.VISIBLE
        currentView.area_main_title_text.text = selectProvince
    }

    private fun backToProvinceActivity(ctx: Context?)
    {
        areaList.clear()
        areaListCity.clear()
        for(i in areaListProvince)  areaList.add(i)
        areaListView.adapter.notifyDataSetChanged()

        currentLevel = LEVEL_PROVINCE
        currentView.area_main_title_backbutton.visibility = View.INVISIBLE
        currentView.area_main_title_text.text = "中国"
    }

    private fun queryProvinceInfo() {
        val address: String = URL_OF_AREA
        queryAreaInfo(address)
    }

    private fun queryCityInfo(province : String, provinceId : Int) {
        val address: String = URL_OF_AREA +"/${provinceId}"
        queryAreaInfo(address)
    }

    private fun queryCountyInfo(provinceId: Int, cityId : Int) {
        val address: String = URL_OF_AREA +"/${provinceId}/${cityId}"
        queryAreaInfo(address)
    }

    private fun queryAreaInfo(address : String){
        HttpUtil.sendOkHttpRequest(address, object: okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                //在这里进行解码Json失败的操作，当前属于子线程
                LogUtil.i("HttpCallback", "queryAreaInfo-onFailure")
                Toast.makeText(ctx, "AreaMainFragment failed to get Area information", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call?, response: Response?) {
                //在这里进行解码Json操作,在这里可以看到解析好的数据，当前属于子线程
                var responseDate : String? = response?.body()?.string()

                LogUtil.i("HttpCallback", "queryAreaInfo-onResponse: ${responseDate}")

                parseJsonWithGSONforAreaList(responseDate)
            }
        })
    }

    private fun parseJsonWithGSONforAreaList(jsonData:String?){

        LogUtil.i("HttpCallback", "Start to praseJsonWithGSON for AreaInfo")

        if (null == jsonData) return

        val gson : Gson = Gson()

        when (currentLevel) {
            LEVEL_PROVINCE -> {
                val myType = object : TypeToken<MutableList<JsonProvince>>(){}.type
                val numbers: MutableList<JsonProvince> = gson.fromJson(jsonData, myType)

                for (i : JsonProvince in numbers)
                {
                    LogUtil.i("HttpCallback", "praseJsonWithGSONfor ${currentLevel}-->${i.id} + ${i.name}")
                    areaList.add(AreaItem(i.name, id = i.id))
                }
            }

            LEVEL_CITY -> {
                val myType = object : TypeToken<MutableList<JsonCity>>(){}.type
                val numbers: MutableList<JsonCity> = gson.fromJson(jsonData, myType)

                for (i : JsonCity in numbers)
                {
                    LogUtil.i("HttpCallback", "praseJsonWithGSONfor ${currentLevel}-->${i.id} + ${i.name}")
                    areaList.add(AreaItem(i.name, id = i.id))
                }
            }

            LEVEL_COUNTRY -> {
                val myType = object : TypeToken<MutableList<JsonCountry>>(){}.type
                val numbers: MutableList<JsonCountry> = gson.fromJson(jsonData, myType)

                for (i : JsonCountry in numbers)
                {
                    LogUtil.i("HttpCallback", "praseJsonWithGSONfor ${currentLevel}-->${i.id} + ${i.name} + ${i.weather_id}")
                    areaList.add(AreaItem(i.name, id = i.id, weatherId = i.weather_id))
                }
            }
        }

        saveAreaListDataToDifferentListAndNotifiyUI(areaList, currentLevel)
    }

    fun getAreaListData():MutableList<AreaItem>{
        return areaList
    }

    fun refreshAreaListData(){
        areaListView.adapter.notifyDataSetChanged()
    }

    fun saveAreaListDataToDifferentListAndNotifiyUI(areaListInput : MutableList<AreaItem>, curlevel: Int){
        when (curlevel) {
            LEVEL_PROVINCE -> {
                for(i in areaList)  areaListProvince.add(i)
                LogUtil.i("HttpCallback", "RunOnUiThread for ${curlevel}, ${areaList.count()}, ${areaListProvince.count()}")
            }

            LEVEL_CITY -> {
                for(i in areaList)  areaListCity.add(i)
                LogUtil.i("HttpCallback", "RunOnUiThread for ${curlevel}, ${areaList.count()}, ${areaListCity.count()}")
            }

            LEVEL_COUNTRY -> {
                for(i in areaList)  areaListCountry.add(i)
                LogUtil.i("HttpCallback", "RunOnUiThread for ${curlevel}, ${areaList.count()}, ${areaListCountry.count()}")
            }
        }

        getActivity()?.runOnUiThread(object :Runnable {
            override fun run() {
                refreshAreaListData()
            }
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        LogUtil.i("AreaMainFragment", "onAttach")
    }

    override fun onPause() {
        super.onPause()
        LogUtil.i("AreaMainFragment", "onPause")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogUtil.i("AreaMainFragment", "onActivityCreated")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.i("AreaMainFragment", "onCreate")
    }

    override fun onStart() {
        super.onStart()
        LogUtil.i("AreaMainFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        LogUtil.i("AreaMainFragment", "onResume")
    }

    override fun onDetach() {
        super.onDetach()
        LogUtil.i("AreaMainFragment", "onDetach")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtil.i("AreaMainFragment", "onDestroyView")
    }

    override fun onStop() {
        super.onStop()
        LogUtil.i("AreaMainFragment", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.i("AreaMainFragment", "onDestroy")
    }
}