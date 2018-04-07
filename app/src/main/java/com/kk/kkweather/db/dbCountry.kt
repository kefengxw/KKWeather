package com.kk.kkweather.db

import org.litepal.crud.DataSupport

data class dbCounty(var id:Int = 0, var countyName:String = "", var countyCode:Int = 0, var cityId:Int = 0, var weatherId:String = "") : DataSupport()