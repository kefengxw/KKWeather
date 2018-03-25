package com.kk.kkweather.util

import android.os.Handler
import android.util.Log

/**
 * Created by xxnfd on 25/03/2018.
 */
class LogUtil {
    companion object {
        
        private val kwlogall : Int = 0
        private val verbosekw : Int= 1
        private val debugkw :Int = 2
        private val infokw : Int = 3
        private val warningkw : Int = 4
        private val errorkw : Int = 5
        private val nonthingkw : Int = 6
        private val loglevelkw : Int = kwlogall
        var handler = Handler()
        var handler1 = Handler()

        var Pid : String = android.os.Process.myPid().toString()
        var Tid : String = android.os.Process.myTid().toString()

        fun v(tag: String, msg: String) {if (loglevelkw <= verbosekw) Log.v(tag+" Pid: ${Pid} Tid + ${Tid}",msg)}
        fun d(tag: String, msg: String) {if (loglevelkw <= debugkw) Log.d(tag+" Pid: ${Pid} Tid + ${Tid}",msg)}
        fun i(tag: String, msg: String) {if (loglevelkw <= infokw) Log.i(tag+" Pid: ${Pid} Tid + ${Tid}",msg)}
        fun w(tag: String, msg: String) {if (loglevelkw <= warningkw) Log.w(tag+" Pid: ${Pid} Tid + ${Tid}",msg)}
        fun e(tag: String, msg: String) {if (loglevelkw <= errorkw) Log.e(tag+" Pid: ${Pid} Tid + ${Tid}",msg)}
    }
}