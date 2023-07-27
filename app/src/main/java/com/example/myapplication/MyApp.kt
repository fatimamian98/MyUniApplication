package com.example.myapplication

import android.app.Application

class MyApp : Application() {
    companion object {
        lateinit var instance: MyApp
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
    }
}