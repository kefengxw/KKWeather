package com.kk.kkweather.db

import org.litepal.crud.DataSupport

data class dbCounty(var id: Int = 0, val countryCode: Int = 0, var countyName: String = "", var cityCode: Int = 0, var weatherCode: String = "") : DataSupport()