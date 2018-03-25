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
        //待考虑，是否把发送消息的这一段代码也放到子线程执行，同时考虑解析json的代码也在子线程执行，尽量UI线程只处理UI的事情
        //这里使用
//        fun sendOkHttpRequest(address: String): String?{
//            val client: OkHttpClient = OkHttpClient()
//            val request: Request = Request.Builder().url(address).build()
//            val call: Call = client.newCall(request)
//            val response : Response = call.execute()
//
//            return response.body()?.string()
//        }

        fun sendOkHttpRequest(address: String, callback: okhttp3.Callback) {
            val client: OkHttpClient = OkHttpClient()
            val request: Request = Request.Builder().url(address).build()
            val call: Call = client.newCall(request)

            call.enqueue(callback)
        }
    }

    /**
     * 网络连接是否正常
     *
     * @return true:有网络    false:无网络
     */
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