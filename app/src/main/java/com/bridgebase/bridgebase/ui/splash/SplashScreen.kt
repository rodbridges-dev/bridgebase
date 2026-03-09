/**
 * BridgeBase Splash Screen
 *
 * Features:
 * - Animated gradient background (dark/light aware)
 * - Logo and tagline fade/scale animations
 * - Navigation handled via Hilt ViewModel
 * - Example auth flow with delayed transition
 *
 * Easily replace `AuthRepository` with your own logic.
 */

package com.bridgebase.bridgebase.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bridgebase.bridgebase.utils.AppBrand
import kotlinx.coroutines.delay
import com.bridgebase.bridgebase.R

/**
 * BridgeBase Splash Screen.
 *
 * Displays the app logo with animated gradient background and fade-in text.
 * Navigation is handled through [SplashViewModel] once the splash delay completes.
 *
 * Features:
 *  - Animated linear gradient (theme-aware)
 *  - Logo + tagline fade/scale animations
 *  - Automatic navigation via StateFlow observers
 */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {

    // 🌈 Animates the background gradient for subtle motion and brand feel
    val infiniteTransition = rememberInfiniteTransition(label = "gradientShift")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f, animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ), label = "gradientOffset"
    )

    // 🧹 Clean up if you ever replace rememberInfiniteTransition with custom coroutines
    DisposableEffect(Unit) {
        onDispose {
            // Currently nothing to explicitly stop — Compose will cancel automatically.
            // But this block is here if you later switch to Animatable.animateTo in a loop.
            println("✅ SplashScreen gradient animation disposed")
        }
    }

    // Theme-aware gradient colors
    val gradientColors = listOf(
        if (isDarkTheme) AppBrand.gradientStartDark else AppBrand.gradientStartLight,
        if (isDarkTheme) AppBrand.gradientEndDark else AppBrand.gradientEndLight
    )

    // Animations for logo and text
    val logoScale = remember { Animatable(0.9f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(20f) }

    LaunchedEffect(Unit) {
        // Animate logo in
        logoAlpha.animateTo(1f, tween(800))
        logoScale.animateTo(1f, tween(800, easing = FastOutSlowInEasing))

        // Delay, then animate text
        delay(400)
        textAlpha.animateTo(1f, tween(600))
        textOffset.animateTo(0f, tween(600, easing = FastOutSlowInEasing))

    }

    // Observe the current UI state emitted by the ViewModel.
    // collectAsStateWithLifecycle() automatically stops collecting when
    // the composable is not active (e.g., during configuration changes).
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Trigger navigation whenever the SplashUiState changes.
    LaunchedEffect(uiState) {
        when (uiState) {
            // User is authenticated → proceed to Home screen
            is SplashUiState.NavigateHome -> navigateToHome()

            // User not authenticated → go to Login screen
            is SplashUiState.NavigateLogin -> navigateToLogin()

            // Default state → do nothing (covers Loading/Error cases)
            else -> Unit
        }
    }

    // UI layout: animated background + centered logo/text
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, offset),
                    end = Offset(0f, offset + 1000f)
                )
            ), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App logo
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer(
                        scaleX = logoScale.value, scaleY = logoScale.value, alpha = logoAlpha.value
                    )
            )
            // App name
            Text(
                text = AppBrand.APPNAME,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .graphicsLayer(
                        translationY = textOffset.value, alpha = textAlpha.value
                    )
            )
            // Tagline
            Text(
                text = AppBrand.TAGLINE,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .graphicsLayer(
                        translationY = textOffset.value, alpha = textAlpha.value
                    )
            )
        }
    }

}
