package com.kk.kkweather.db

import org.litepal.crud.DataSupport

data class dbProvince(val id:Int = 0, val provinceCode: Int = 0, val provinceName:String = "", val nationCode: Int = 0) : DataSupport()