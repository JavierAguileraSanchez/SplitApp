package com.example.splitapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Violet400,
    onPrimary = Color(0xFF1E0A4A),
    primaryContainer = Violet700,
    onPrimaryContainer = Violet200,
    secondary = Emerald400,
    onSecondary = Color(0xFF064E3B),
    tertiary = Emerald400,
    onTertiary = Color(0xFF064E3B),
    tertiaryContainer = Color(0xFF065F46),
    onTertiaryContainer = EmeraldContainer,
    error = Color(0xFFF87171),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
    background = DarkBackground,
    onBackground = Color(0xFFF5F3FF),
    surface = DarkSurface,
    onSurface = Color(0xFFF5F3FF),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC4B5FD),
    outline = Color(0xFF6D28D9)
)

private val LightColorScheme = lightColorScheme(
    primary = Violet600,
    onPrimary = Color.White,
    primaryContainer = Violet100,
    onPrimaryContainer = Color(0xFF3B0764),
    secondary = Emerald600,
    onSecondary = Color.White,
    tertiary = Emerald600,
    onTertiary = Color.White,
    tertiaryContainer = EmeraldContainer,
    onTertiaryContainer = OnEmeraldContainer,
    error = Red600,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = Color(0xFFFAF9FF),
    onBackground = Color(0xFF1C1040),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1040),
    surfaceVariant = Color(0xFFF3F0FF),
    onSurfaceVariant = Color(0xFF5B4A8A),
    outline = Color(0xFFD4BCFD)
)

@Composable
fun SplitAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
