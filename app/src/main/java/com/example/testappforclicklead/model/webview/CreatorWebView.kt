package com.example.testappforclicklead.model.webview

import android.content.Context
import android.net.Uri
import android.os.Message
import android.view.View
import android.view.Window
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import com.example.testappforclicklead.model.repository.Repository
import com.example.testappforclicklead.view.interfaceView.InterfaceSplashActivity

class CreatorWebView(private val interfaceActivity:InterfaceSplashActivity, private val context: Context, private val window: Window) {

    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null
    private val fileChooserResultCode = 1
    private var customView: View? = null

    private val repository = Repository(context)

    //функция создания WebView
    fun createWebView(): WebView {
        var webView = WebView(context) // создание нового WebView
        webView = setSettingsForWebView(webView) // установка настроек для WebView
        return webView
    }

    // функция установки настроек к созданному WebView
    private fun setSettingsForWebView(webView:WebView):WebView{

        var webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportMultipleWindows(true)
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.domStorageEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.databaseEnabled = true
        webSettings.databasePath = context.getDir("webview_databases", 0).path
        webSettings.allowFileAccess = true
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.loadsImagesAutomatically = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if(url!=null){
                    repository.saveLastUrl(url) //сохранение новой открывшейся ссылки
                }
                return false
            }
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()
                if(url!=null){
                    repository.saveLastUrl(url) //сохранение новой открывшейся ссылки
                }
                return false
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (fileUploadCallback != null) {
                    fileUploadCallback!!.onReceiveValue(null)
                    fileUploadCallback = null
                }

                fileUploadCallback = filePathCallback

                val intent = fileChooserParams.createIntent()
                try {
                    @Suppress("DEPRECATION")
                    interfaceActivity.goActivityForResult(intent,fileChooserResultCode)
                } catch (e: Exception) {
                    fileUploadCallback = null
                    return false
                }

                return true
            }

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                super.onShowCustomView(view, callback)

                // полноэкранный режим + скрытие панели кнопок
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

                if (customView != null) {
                    callback.onCustomViewHidden()
                    return
                }

                customView = view
                customView?.let {
                    val decorView = window.decorView as FrameLayout
                    decorView.addView(it, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    interfaceActivity.setHorizontalScreen() // установка горизонтального режима экрана
                }
                webView.visibility = View.GONE

            }

            override fun onHideCustomView() {
                super.onHideCustomView()

                customView?.let {
                    val decorView = window.decorView as FrameLayout
                    decorView.removeView(it)
                    interfaceActivity.setVerticalScreen() // установка портретного режима экрана
                    customView = null
                }
                webView.visibility = View.VISIBLE

            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val href = view!!.handler.obtainMessage()
                view.requestFocusNodeHref(href)
                val url = href.data.getString("url") // получение url адреса для нового окна
                interfaceActivity.showNewWindow(url!!) // открытие нового окна
                return true
            }

        }

        return webView

    }

}