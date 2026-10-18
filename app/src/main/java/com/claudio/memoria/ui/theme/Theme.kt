package com.claudio.memoria.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = OnGreen,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenDark,
    secondary = WarmOrange,
    onSecondary = OnGreen,
    secondaryContainer = WarmContainer,
    onSecondaryContainer = Ink,
    background = Cream,
    onBackground = Ink,
    surface = SurfaceWhite,
    onSurface = Ink,
    surfaceVariant = GreenContainer,
    onSurfaceVariant = InkSoft,
    outline = Divider,
    error = Danger,
    onError = OnGreen
)

@Composable
fun MemoriaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        typography = AppTypography,
        content = content
    )
}
