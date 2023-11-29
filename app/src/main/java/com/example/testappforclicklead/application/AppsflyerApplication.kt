package com.example.testappforclicklead.application

import android.app.Application
import com.example.testappforclicklead.model.appsflyer.Appsflyer

class AppsflyerApplication:Application() {

    private lateinit var appsflyer: Appsflyer

    override fun onCreate() {
        super.onCreate()

        appsflyer = Appsflyer(this)

        appsflyer.initAppsflyer()  // инициализация Appsflyer
        appsflyer.startAppsflyer() // старт Appsflyer

    }

}