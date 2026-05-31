package com.example.splitapp

import android.app.Application
import com.example.splitapp.util.AnalyticsHelper
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try { AnalyticsHelper.init(this) } catch (_: Throwable) {}
        CoroutineScope(Dispatchers.Default).launch {
            try {
                FirebaseApp.initializeApp(this@SplitApplication)
            } catch (_: Throwable) {}
        }
    }
}
