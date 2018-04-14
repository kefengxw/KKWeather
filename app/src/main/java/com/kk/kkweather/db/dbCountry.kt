package com.kk.kkweather.db

import org.litepal.crud.DataSupport

data class dbCounty(val id: Int = 0, val countryCode: Int = 0, val countyName: String = "", val cityCode: Int = 0, val weatherCode: String = "") : DataSupport()