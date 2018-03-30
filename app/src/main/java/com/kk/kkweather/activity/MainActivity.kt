package com.kk.kkweather.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import com.kk.kkweather.util.LogUtil
import android.widget.Toast
import com.kk.kkweather.R


class MainActivity : AppCompatActivity() {

    var ctx = this
    var ctxtest = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.amframelayout)
    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(ctx, "Hi MainActivity onStart", Toast.LENGTH_SHORT).show()
        LogUtil.i("KW-MainActivity", "MainActivity onStart")
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(ctx, "Hi MainActivity onResume", Toast.LENGTH_SHORT).show()
        LogUtil.i("KW-MainActivity", "MainActivity onResume")
    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(ctx, "Hi MainActivity onPause", Toast.LENGTH_SHORT).show()
        LogUtil.i("KW-MainActivity", "MainActivity onPause")
    }

    override fun onStop() {
        super.onStop()
        Toast.makeText(ctx, "Hi MainActivity onStop", Toast.LENGTH_SHORT).show()
        LogUtil.i("KW-MainActivity", "MainActivity onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(ctx, "Hi MainActivity onDestroy", Toast.LENGTH_SHORT).show()
        LogUtil.i("KW-MainActivity", "MainActivity onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Toast.makeText(ctx, "Hi MainActivity onRestart", Toast.LENGTH_SHORT).show()
        LogUtil.i("KW-MainActivity", "MainActivity onRestart")
    }
}