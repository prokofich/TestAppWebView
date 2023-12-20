
package com.example.testappforclicklead.model.repository

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.provider.Settings
import com.example.testappforclicklead.model.constant.COUNT_START_APPLICATION
import com.example.testappforclicklead.model.constant.LAST_URL_IN_WEB
import com.example.testappforclicklead.model.constant.MAIN_ATTRIBUTE
import com.example.testappforclicklead.model.constant.MAIN_URL_IN_WEB
import com.example.testappforclicklead.model.constant.STATUS_INSTALLATION
import com.example.testappforclicklead.model.constant.USER_ID
import java.util.UUID

class Repository(context: Context){

    @Suppress("DEPRECATION")
    //использование SharedPreferences в качестве базы данных
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    //функция сохранения последнего url адреса
    fun saveLastUrl(url:String){
        sharedPreferences.edit()
            .putString(LAST_URL_IN_WEB,url)
            .apply()
    }

    //функция сохранения первого полученного url адреса
    fun saveMainUrl(url: String){
        sharedPreferences.edit()
            .putString(MAIN_URL_IN_WEB,url)
            .apply()
    }

    //функция сохранение главного атрибута
    fun saveMainAttribute(nameCompany:String){
        sharedPreferences.edit()
            .putString(MAIN_ATTRIBUTE,nameCompany)
            .apply()
    }

    //функция сохранения статуса установки
    fun saveStatusInstallation(status:String){
        sharedPreferences.edit()
            .putString(STATUS_INSTALLATION,status)
            .apply()
    }

    //функция обновления количества запусков приложения
    fun updateCountStartApplication(){
        sharedPreferences.edit()
            .putInt(COUNT_START_APPLICATION,getCountStartApplication()+1)
            .apply()
    }

    //функция получения последнего url адреса
    fun getLastUrl(): String {
        return sharedPreferences.getString(LAST_URL_IN_WEB,"").toString()
    }

    //функция получения главного атрибута
    fun getMainAttribute(): String {
        return sharedPreferences.getString(MAIN_ATTRIBUTE,"").toString()
    }

    //функция получения первого полученного url адреса
    fun getMainUrl(): String {
        return sharedPreferences.getString(MAIN_URL_IN_WEB,"").toString()
    }

    //функция получения количества запусков приложения
    private fun getCountStartApplication(): Int {
        return sharedPreferences.getInt(COUNT_START_APPLICATION,0)
    }

    //функция проверки первого запуска приложения
    fun checkFirstStartApplication(): Boolean {
        return getCountStartApplication()==0
    }

    //функция создания кастомного ID юзера для OneSignal
    fun createAndSaveUserId(){
        val userId = UUID.randomUUID().toString()
        sharedPreferences.edit()
            .putString(USER_ID,userId)
            .apply()
    }

    //функция получения кастомного ID юзера
    fun getUserId():String{
        return sharedPreferences.getString(USER_ID,"").toString()
    }

}