package com.kk.kkweather.db

import org.litepal.crud.DataSupport

data class dbCity(val id: Int = 0, val cityCode: Int = 0, val cityName: String = "", val provinceCode: Int = 0) : DataSupport()