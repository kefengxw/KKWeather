package com.kk.kkweather.activity

import kotlinx.android.synthetic.main.area_item.view.*
import com.kk.kkweather.R

/**
 * Created by xxnfd on 25/03/2018.
 */
data class AreaItem(var name : String, var imageId :Int = R.drawable.ic_home, var id: Int = 0, var weatherId : String = "")