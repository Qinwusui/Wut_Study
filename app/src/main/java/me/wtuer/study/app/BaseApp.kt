package me.wtuer.study.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class BaseApp : Application() {
    @SuppressLint("StaticFieldLeak")
    companion object{
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
    }
}