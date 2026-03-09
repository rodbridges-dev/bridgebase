package com.bridgebase.bridgebase.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.bridgebase.bridgebase.utils.AppBrand

val DarkColors = darkColorScheme(
    primary = AppBrand.primaryDark,
    onPrimary = Color.Black,

    surface = AppBrand.surfaceDark,
    onSurface = AppBrand.onSurfaceDark,

    background = AppBrand.surfaceDark,
    onBackground = AppBrand.onSurfaceDark,

    secondary = AppBrand.primaryDark.copy(alpha = 0.7f),
    outline = Color(0xFF444444),

    surfaceVariant = AppBrand.cardDark,
    onSurfaceVariant = AppBrand.onCardDark,
)