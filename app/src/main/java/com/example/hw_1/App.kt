package com.example.hw_1

import Models.Location
import Utilites.SharedPreferencesManager
import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManager.init(this)
    }
}