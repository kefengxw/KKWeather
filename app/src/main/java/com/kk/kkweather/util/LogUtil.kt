package com.kk.kkweather.util

import android.os.Handler
import android.util.Log

/**
 * Created by xxnfd on 25/03/2018.
 */
class LogUtil {
    companion object {
        private val verbosekkw: Int = 1
        private val debugkkw: Int = 2
        private val infokkw: Int = 3
        private val warningkkw: Int = 4
        private val errorkkw: Int = 5

        val kkwlogall: Int = 0
        val kkwnonthing: Int = 6
        var kkwloglevel: Int = kkwlogall

        val Pid: String = android.os.Process.myPid().toString()
        val Tid: String = android.os.Process.myTid().toString()

        fun v(tag: String, msg: String) {
            if (kkwloglevel <= verbosekkw) Log.v(tag + " Pid: ${Pid} Tid + ${Tid}", msg)
        }

        fun d(tag: String, msg: String) {
            if (kkwloglevel <= debugkkw) Log.d(tag + " Pid: ${Pid} Tid + ${Tid}", msg)
        }

        fun i(tag: String, msg: String) {
            if (kkwloglevel <= infokkw) Log.i(tag + " Pid: ${Pid} Tid + ${Tid}", msg)
        }

        fun w(tag: String, msg: String) {
            if (kkwloglevel <= warningkkw) Log.w(tag + " Pid: ${Pid} Tid + ${Tid}", msg)
        }

        fun e(tag: String, msg: String) {
            if (kkwloglevel <= errorkkw) Log.e(tag + " Pid: ${Pid} Tid + ${Tid}", msg)
        }
    }
}