package com.example.splitapp.util

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsHelper {

    private lateinit var analytics: FirebaseAnalytics

    fun init(context: Context) {
        analytics = FirebaseAnalytics.getInstance(context)
    }

    fun logGroupCreated() = log("group_created")
    fun logExpenseAdded() = log("expense_added")
    fun logDebtSettled() = log("debt_settled")
    fun logGroupJoined() = log("group_joined")

    private fun log(event: String, params: Bundle? = null) {
        if (::analytics.isInitialized) analytics.logEvent(event, params)
    }
}
