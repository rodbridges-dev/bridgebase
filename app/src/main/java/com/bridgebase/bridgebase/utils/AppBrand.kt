package com.bridgebase.bridgebase.utils

import androidx.compose.ui.graphics.Color

object AppBrand {

    // Main brand identity color
    val primary = Color(0xFF2A7DE1)

    // Text color in light and dark mode
    val surfaceLight = Color(0xFFFFFFFF)
    val surfaceDark = Color(0xFF1A1A1A)

    // Light mode text
    val onPrimaryLight = Color.White
    val onSurfaceLight = Color(0xFF1A1A1A)

    // Dark mode text
    val primaryDark = Color(0xFF90CAF9)
    val onSurfaceDark = Color(0xFFAFAFAF)

    // Light gradient
    val gradientStartLight = Color(0xFF2D2EFF)
    val gradientEndLight = Color(0xFF5BA4FF)

    // Dark gradient
    val gradientStartDark = Color(0xFF1A1A8F)
    val gradientEndDark = Color(0xFF3666B3)

    // Login page
    val gradientStartLoginLight = Color(0xFF5BA4FF)
    val gradientEndLoginLight = Color.White
    val gradientStartLoginDark = Color(0xFF0E0E4F)
    val gradientEndLoginDark = Color.Black
    val buttonDisabledLight = Color(0xFFE6E9F5)
    val buttonDisabledDark = Color(0xFF2D2EFF).copy(alpha = 0.3f)

    // Journal Card Colors
    val cardLight = Color(0xFFFFFFFF)
    val onCardLight = Color(0xFF1A1A1A)
    val cardDark = Color(0xFF2C2C2C)
    val onCardDark = Color(0xFFAFAFAF)

    val emptyStateBackgroundLight = Color(0xFFF7F9FB)
    val emptyStateBackgroundDark = Color(0xFF1E1F22)

    val emptyStateBorderLight = Color(0xFFDDE2E8)
    val emptyStateBorderDark = Color(0xFF2C2D31)

    // Brand name + tagline
    const val APPNAME = "BridgeBase"
    const val TAGLINE = "Your foundation for building connected apps"
}