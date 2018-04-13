package com.kk.kkweather.util

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import okhttp3.Response

/**
 * Created by xxnfd on 25/03/2018.
 */
class HttpUtil {

    companion object {
        //val response : Response = call.execute()
        fun sendOkHttpRequest(address: String, callback: okhttp3.Callback) {
            val client: OkHttpClient = OkHttpClient()
            val request: Request = Request.Builder().url(address).build()
            val call: Call = client.newCall(request)

            call.enqueue(callback)
        }
    }

//check the network works or not
//    fun isNetworkConnected(context: Context?): Boolean {
//        if (context != null) {
//            val mConnectivityManager = context!!
//                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            val mNetworkInfo = mConnectivityManager.activeNetworkInfo
//            if (mNetworkInfo != null) {
//                return mNetworkInfo.isAvailable
//            }
//        }
//        return false
//    }
}