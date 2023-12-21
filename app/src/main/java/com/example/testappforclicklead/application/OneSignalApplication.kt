package com.example.testappforclicklead.application

import android.app.Application
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.example.testappforclicklead.model.constant.ONESIGNAL_APP_ID
import com.example.testappforclicklead.model.repository.Repository
import com.onesignal.OneSignal

class OneSignalApplication:Application() {

    private lateinit var repository:Repository
    override fun onCreate() {
        super.onCreate()

        repository = Repository(this)

        if(repository.getUserId()==""){
            repository.createAndSaveUserId()
        }

        // OneSignal
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        OneSignal.initWithContext(this)
        OneSignal.setExternalUserId(repository.getUserId())
        OneSignal.promptForPushNotifications()

    }
}