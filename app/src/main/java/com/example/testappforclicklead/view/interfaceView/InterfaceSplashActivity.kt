package com.example.testappforclicklead.view.interfaceView

import android.content.Intent
import android.webkit.WebView

interface InterfaceSplashActivity {

    fun loadUrlInWebView(url:String)                 // функция загрузки url адреса,полученного из DatabaseFirestore, для WebView
    fun goActivityForResult(intent: Intent,code:Int) // функция запуска activity
    fun setHorizontalScreen()                        // установка горизонтального режима экрана
    fun setVerticalScreen()                          // установка портретного режима экрана
    fun goToWhiteScreen()                            // переход на заглушку
    fun showNewWindow(url: String)                   // показ нового окна

}