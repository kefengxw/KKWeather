package com.kk.kkweather.db

import org.litepal.crud.DataSupport

data class dbCity(var id: Int = 0, val cityCode: Int = 0, var cityName: String = "", var provinceCode: Int = 0) : DataSupport()