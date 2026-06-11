package com.example.splitapp

import android.app.Application
import com.example.splitapp.util.AnalyticsHelper
import com.google.firebase.FirebaseApp

class SplitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try { FirebaseApp.initializeApp(this) } catch (_: Throwable) {}
        try { AnalyticsHelper.init(this) } catch (_: Throwable) {}
    }
}
