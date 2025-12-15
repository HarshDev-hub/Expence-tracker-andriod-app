package com.example.expencetracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val AppDarkColorScheme = darkColorScheme(
    primary = Color(0xFF5B8DEE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2A2A2A),
    secondary = Color(0xFF00C9A7),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2A2A2A),
    outline = Color(0xFF3A3A3A)
)

private val AppLightColorScheme = lightColorScheme(
    primary = Color(0xFF5B8DEE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F1FF),
    secondary = Color(0xFF00C9A7),
    background = Color(0xFFF8F9FD),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1A2138),
    onSurface = Color(0xFF1A2138),
    surfaceVariant = Color(0xFFF8F9FD),
    outline = Color(0xFFE0E0E0)
)

@Composable
fun ExpenceTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AppDarkColorScheme
        else -> AppLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}