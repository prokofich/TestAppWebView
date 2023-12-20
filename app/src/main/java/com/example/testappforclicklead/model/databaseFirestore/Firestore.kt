package com.example.testappforclicklead.model.databaseFirestore

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.testappforclicklead.model.constant.NOT_ORGANIC_INSTALL
import com.example.testappforclicklead.model.constant.ORGANIC_INSTALL
import com.example.testappforclicklead.model.repository.Repository
import com.example.testappforclicklead.view.interfaceView.InterfaceSplashActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Firestore(private val interfaceActivity:InterfaceSplashActivity, context: Context) {

    private val database = FirebaseFirestore.getInstance()
    private var repository = Repository(context)

    //функция получения ссылок из DatabaseFirestore
    fun getUrlFromDatabase(status:String,list:List<String>?){
        CoroutineScope(Dispatchers.IO).launch {
            database.collection("URL_FOR_WEBVIEW")
                .document("URL")
                .get()
                .addOnSuccessListener { data ->
                    when(status){

                        // получение ссылки при органической установке
                        ORGANIC_INSTALL -> {
                            Handler(Looper.getMainLooper()).post {
                                if(data["url1"]!=""){
                                    var newUrl = data["url1"].toString()
                                    repository.saveMainUrl(newUrl)    // сохранение полученной ссылки
                                    repository.saveLastUrl(newUrl)    // сохранение полученной ссылки
                                    interfaceActivity.loadUrl(newUrl) // загрузка готовой ссылки
                                }else{
                                    //была получена пустая ссылка
                                    interfaceActivity.goToWhiteScreen() // переход на заглушку,если ссылка пустая
                                }
                            }
                        }

                        // получение ссылки при неорганической установке
                        NOT_ORGANIC_INSTALL -> {
                            Handler(Looper.getMainLooper()).post {
                                if (data["url2"]!=""){
                                    var newUrl = data["url2"].toString()+"/1238sdfu8?"
                                    // вставка значений в сырую ссылку
                                    for(i in 0..<(list!!.size)){
                                        val index = i
                                        newUrl+="sub${index+1}=${list[i]}&"
                                    }
                                    newUrl = newUrl.removeSuffix("&")

                                    repository.saveMainUrl(newUrl)    // сохранение полученной ссылки
                                    repository.saveLastUrl(newUrl)    // сохранение полученной ссылки
                                    interfaceActivity.loadUrl(newUrl) // загрузка готовой ссылки
                                }else{
                                    // была получена пустая ссылка
                                    interfaceActivity.goToWhiteScreen() // переход на заглушку,если ссылка пустая
                                }
                            }
                        }

                    }
                }
        }
    }

}