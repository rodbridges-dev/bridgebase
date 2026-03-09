package com.bridgebase.bridgebase.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GoalShimmer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFE6E6E6),
                        Color(0xFFF2F2F2),
                        Color(0xFFE6E6E6)
                    )
                )
            )
    )
}

@Composable
fun JournalShimmerCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp) // matches average journal entry card size
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFE6E6E6),
                        Color(0xFFF2F2F2),
                        Color(0xFFE6E6E6)
                    )
                )
            )
    )
}

@Composable
fun HomeShimmer() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.height(24.dp))

        // HEADER SHIMMER
        Row(verticalAlignment = Alignment.CenterVertically) {

            ShimmerBox(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.width(16.dp))

            Column {
                ShimmerBox(
                    modifier = Modifier
                        .height(22.dp)
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(6.dp))
                )
                Spacer(Modifier.height(8.dp))
                ShimmerBox(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.7f)
                        .clip(RoundedCornerShape(6.dp))
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // JOURNAL CTA CARD SHIMMER
        ShimmerCard(height = 110.dp)

        Spacer(Modifier.height(24.dp))

        // GOALS SUMMARY SHIMMER
        ShimmerCard(height = 140.dp)

        Spacer(Modifier.height(24.dp))

        // RECENT ACTIVITY HEADER
        ShimmerBox(
            modifier = Modifier
                .height(24.dp)
                .fillMaxWidth(0.4f)
                .clip(RoundedCornerShape(6.dp))
        )

        Spacer(Modifier.height(16.dp))

        // 3 ACTIVITY ROW SHIMMERS
        repeat(3) {
            ShimmerCard(height = 70.dp)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ShimmerCard(height: Dp) {
    ShimmerBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(22.dp))
    )
}

@Composable
private fun ShimmerBox(modifier: Modifier) {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            tween(900),
            RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .background(Color.Gray.copy(alpha = alpha))
    )
}

