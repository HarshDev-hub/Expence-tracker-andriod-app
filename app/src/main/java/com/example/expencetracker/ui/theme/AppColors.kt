package com.example.expencetracker.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light Theme Colors
private val LightPrimary = Color(0xFF5B8DEE)
private val LightPrimaryVariant = Color(0xFF4A7BD9)
private val LightSecondary = Color(0xFF00C9A7)
private val LightBackground = Color(0xFFF8F9FD)
private val LightSurface = Color(0xFFFFFFFF)
private val LightOnBackground = Color(0xFF1A2138)
private val LightOnSurface = Color(0xFF1A2138)
private val LightTextSecondary = Color(0xFF8F92A1)

// Dark Theme Colors
private val DarkPrimary = Color(0xFF5B8DEE)
private val DarkPrimaryVariant = Color(0xFF4A7BD9)
private val DarkSecondary = Color(0xFF00C9A7)
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF1E1E1E)
private val DarkOnBackground = Color(0xFFE0E0E0)
private val DarkOnSurface = Color(0xFFE0E0E0)
private val DarkTextSecondary = Color(0xFFB0B0B0)

val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F1FF),
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    surfaceVariant = Color(0xFFF8F9FD),
    outline = Color(0xFFE0E0E0)
)

val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2A2A2A),
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF2A2A2A),
    outline = Color(0xFF3A3A3A)
)

// Accent colors that don't change with theme
object AccentColors {
    val Green = Color(0xFF00C9A7)
    val Orange = Color(0xFFFF9671)
    val Red = Color(0xFFFF6B6B)
    val Purple = Color(0xFF7A288A)
    val Blue = Color(0xFF5B8DEE)
}