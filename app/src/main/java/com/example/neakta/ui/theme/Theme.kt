package com.example.neakta.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary      = NeakTaPrimary,
    secondary    = NeakTaPrimary,
    tertiary     = NeakTaPrimary,
    background   = NeakTaBackground,
    surface      = NeakTaSurfaceStrong,
    onPrimary    = NeakTaBackground,
    onSecondary  = NeakTaBackground,
    onTertiary   = NeakTaBackground,
    onBackground = NeakTaTextPrimary,
    onSurface    = NeakTaTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary      = NeakTaPrimaryLight,
    secondary    = NeakTaPrimaryLight,
    tertiary     = NeakTaPrimaryLight,
    background   = NeakTaBackgroundLight,
    surface      = NeakTaSurfaceStrongLight,
    onPrimary    = NeakTaBackgroundLight,
    onSecondary  = NeakTaBackgroundLight,
    onTertiary   = NeakTaBackgroundLight,
    onBackground = NeakTaTextPrimaryLight,
    onSurface    = NeakTaTextPrimaryLight
)

@Composable
fun NeakTaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,  // always dark
        typography  = Typography,
        content     = content
    )
}