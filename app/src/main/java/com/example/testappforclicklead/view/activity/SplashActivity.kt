package com.example.testappforclicklead.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import com.appsflyer.AppsFlyerConversionListener
import com.example.testappforclicklead.databinding.ActivitySplashBinding
import com.example.testappforclicklead.model.constant.APPSFLYER
import com.example.testappforclicklead.model.constant.DEEPLINK_FACEBOOK
import com.example.testappforclicklead.model.constant.NOT_ORGANIC_INSTALL
import com.example.testappforclicklead.model.constant.ORGANIC_INSTALL
import com.example.testappforclicklead.model.repository.Repository
import com.example.testappforclicklead.model.databaseFirestore.Firestore
import com.example.testappforclicklead.model.webview.CreatorWebView
import com.example.testappforclicklead.view.interfaceView.InterfaceSplashActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(),InterfaceSplashActivity {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var webView: WebView
    private lateinit var repository: Repository
    private lateinit var firestore:Firestore
    private lateinit var creatorWebView: CreatorWebView

    private val webViewArray = mutableListOf<WebView>()
    private var jobTime:Job = Job() // таймер для проверки органической установки

    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null
    private val fileChooserResultCode = 1

    private var flagAppsflyer = false        // проверка были ли получены данные из Appsflyer(по умолчанию не получены)
    private var flagDeeplinkFacebook = false // проверка были ли получены данные из Deeplink Facebook(по умолчанию не получены)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firestore = Firestore(this,this)
        repository = Repository(this)
        creatorWebView = CreatorWebView(this,this,window)

        // загрузка состояния WebView после смены конфигурации
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        }

        //проверка первого запуска приложения
        if(repository.checkFirstStartApplication()){

            repository.updateCountStartApplication() // обновление количества запусков приложения
            webView = creatorWebView.createWebView() // создание WebView

            getAttributeFromAppsflyer() // получение атрибутов от Appsflyer
            getAttributeFromDeeplinkFacebook() // получение атрибутов от Deeplink Facebook
            checkOrganicInstall() // проверка органической установки
        }else{
            // действия при повторном запуске приложения
            webView = creatorWebView.createWebView() //создание WebView
            loadUrlInWebView(repository.getLastUrl()) // открытие последней ссылки при повторном запуске приложения
        }

    }

    //функция добавления ссылки к WebView
    private fun loadUrlInWebView(url:String){
        webView.loadUrl(url) //добавление ссылки к WebView
        binding.idSplash.addView(webView) //добавление созданного WebView на экран
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileChooserResultCode) {
            if (fileUploadCallback != null) {
                val results = WebChromeClient.FileChooserParams.parseResult(resultCode, data)
                fileUploadCallback!!.onReceiveValue(results)
                fileUploadCallback = null
            }
        }
    }

    //сохранение состояния при изменении конфигурации
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    //обработка перехода назад + возможное закрытие
    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val currentWebView = webViewArray.lastOrNull()
        if (webViewArray.size > 1) {
            val index = webViewArray.indexOf(currentWebView)
            webViewArray.removeAt(index) // удаляем последний WebView из списка
            binding.idSplash.removeView(currentWebView) // удаляем WebView с экрана

            val previousWebView = webViewArray.last()
            binding.idSplash.addView(previousWebView) // показываем на экране предыдущий WebView
        } else {
            super.onBackPressed()
        }
    }

    //функция загрузки url адреса
    override fun loadUrl(url: String) {
        loadUrlInWebView(url)
    }

    override fun goActivityForResult(intent: Intent, code: Int) {
        startActivityForResult(intent, code)
    }

    //установка горизонтального режима для экрана
    override fun setHorizontalScreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    //установка вертикального режима для экрана
    override fun setVerticalScreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    //функция перехода пользователя на белую часть(заглушка)
    override fun goToWhiteScreen(){
        startActivity(Intent(this,MainActivity::class.java))
    }

    // функция добавления нового WebView на экран
    override fun addWebViewInList(webView: WebView) {
        webViewArray.add(webView) // добавление в список
        Toast.makeText(this,webViewArray.size,Toast.LENGTH_SHORT).show()
        binding.idSplash.addView(webView) // добавление на экран
    }

    // функция получения атрибутов от Appsflyer
    private fun getAttributeFromAppsflyer(){
        val conversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                p0?.let { attributionData ->
                    val campaign = attributionData["campaign"].toString() // название компании
                    /*if(campaign!=""){

                        flagAppsflyer = true // атрибуты от Appsflyer получены(Deeplink Facebook не нужен)
                        repository.saveMainAttribute(campaign) // сохранение главного атрибута
                        repository.saveStatusInstallation(NOT_ORGANIC_INSTALL) // сохранение статуса неорганическая установка

                        val parts = campaign.split("_") // разбиение названия на 6 частей
                        firestore.getUrlFromDatabase(APPSFLYER,parts) // функция загрузки сырой ссылки с обработкой

                    }*/
                }
            }
            override fun onConversionDataFail(p0: String?) {}
            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}
            override fun onAttributionFailure(p0: String?) {}
        }
    }

    // функция получения атрибутов из Deeplink Facebook
    private fun getAttributeFromDeeplinkFacebook(){
        if(!flagAppsflyer){
            val data: Uri? = intent.data
            if (data != null) {
                // Обработка данных, переданных через DeepLink Facebook
                val campaign: String? = data.getQueryParameter("campaign") // название компании

                if (campaign!=null){
                    if (campaign!=""){
                        campaign.removePrefix("app://") // удаление ненужной части
                        flagDeeplinkFacebook = true
                        repository.saveStatusInstallation(NOT_ORGANIC_INSTALL) // сохранение статуса неорганическая установка
                        repository.saveMainAttribute(campaign) // сохранение главного атрибута
                        val parts = campaign.split("_") // разбиение названия на части
                        firestore.getUrlFromDatabase(DEEPLINK_FACEBOOK,parts) // загрузка сырой ссылки с обработкой
                    }
                }
            }
        }
    }

    // функция проверки органической установки
    private fun checkOrganicInstall(){
        jobTime = CoroutineScope(Dispatchers.Main).launch {
            delay(10000) // тайм-аут 10 секунд
            if(!(flagAppsflyer || flagDeeplinkFacebook)){
                repository.saveStatusInstallation(ORGANIC_INSTALL) // сохранение статуса органическая установка, если данные не пришли
                firestore.getUrlFromDatabase(ORGANIC_INSTALL,null) // загрузка сырой ссылки
            }
        }
    }

}
