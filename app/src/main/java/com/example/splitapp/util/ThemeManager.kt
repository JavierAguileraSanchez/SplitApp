package com.example.splitapp.util

import android.content.Context

object ThemeManager {
    const val THEME_DARK = "dark"
    const val THEME_LIGHT = "light"

    private const val PREFS = "split_app_prefs"
    private const val KEY_GLOBAL = "theme"

    private fun userKey(userId: String) = "theme_$userId"

    fun getTheme(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_GLOBAL, THEME_LIGHT) == THEME_DARK

    fun setTheme(context: Context, isDark: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_GLOBAL, if (isDark) THEME_DARK else THEME_LIGHT).apply()
    }

    fun getUserTheme(context: Context, userId: String): Boolean? {
        if (userId.isBlank()) return null
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return if (prefs.contains(userKey(userId)))
            prefs.getString(userKey(userId), THEME_LIGHT) == THEME_DARK
        else
            null
    }

    fun setUserTheme(context: Context, userId: String, isDark: Boolean) {
        if (userId.isBlank()) return
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(userKey(userId), if (isDark) THEME_DARK else THEME_LIGHT).apply()
    }
}
