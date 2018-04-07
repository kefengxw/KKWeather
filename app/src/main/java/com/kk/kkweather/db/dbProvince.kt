package com.kk.kkweather.db

import org.litepal.crud.DataSupport

data class dbProvince(var id:Int = 0, var provinceName:String = "", var proviceCode:Int = 0) : DataSupport()