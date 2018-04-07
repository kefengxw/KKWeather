package com.kk.kkweather.db

import org.litepal.crud.DataSupport

data class dbCity(var id:Int = 0, var cityName:String = "", var cityCode:Int = 0, var provinceId:Int = 0) : DataSupport()