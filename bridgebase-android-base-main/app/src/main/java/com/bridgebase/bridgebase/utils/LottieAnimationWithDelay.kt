package com.bridgebase.bridgebase.utils

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

@Composable
fun LottieAnimationWithDelay(
    @RawRes animationRes: Int,
    modifier: Modifier = Modifier,
    iterations: Int = 1,
    delayMillis: Long = 0L,
    loop: Boolean = false
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes))
    val animationState = rememberLottieAnimatable()

    LaunchedEffect(composition) {
        if (composition != null) {
            delay(delayMillis) // Wait before starting
            animationState.animate(
                composition = composition,
                iterations = if (loop) LottieConstants.IterateForever else iterations
            )
        }
    }

    @Suppress("DEPRECATION")
    LottieAnimation(
        composition = composition,
        progress = animationState.progress,
        modifier = modifier
    )
}