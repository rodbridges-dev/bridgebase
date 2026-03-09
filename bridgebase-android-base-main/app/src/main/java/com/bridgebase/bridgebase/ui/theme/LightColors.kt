package com.bridgebase.bridgebase.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.bridgebase.bridgebase.utils.AppBrand

val LightColors = lightColorScheme(
    primary = AppBrand.primary,
    onPrimary = Color.White,

    surface = AppBrand.surfaceLight,
    onSurface = AppBrand.onSurfaceLight,

    background = AppBrand.surfaceLight,
    onBackground = AppBrand.onSurfaceLight,

    secondary = AppBrand.primary.copy(alpha = 0.7f),
    outline = Color(0xFFDDDDDD),

    surfaceVariant = AppBrand.cardLight,
    onSurfaceVariant = AppBrand.onCardLight,
)