package com.example.testappforclicklead.model.appsflyer

import android.content.Context
import com.appsflyer.AppsFlyerLib

class Appsflyer(private val context: Context) {

    // функция инициализации Appsflyer
    fun initAppsflyer(){
        AppsFlyerLib.getInstance().init("MY_KEY", null, context) // вместо MY_KEY необходимо будет подставить реальное значение
    }

    // функция старта Appsflyer
    fun startAppsflyer(){
        AppsFlyerLib.getInstance().start(context)
    }

}