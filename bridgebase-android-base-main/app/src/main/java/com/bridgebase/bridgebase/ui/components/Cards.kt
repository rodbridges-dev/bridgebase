package com.bridgebase.bridgebase.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * BridgeBaseCard
 *
 * A reusable card component that centralizes:
 * - corner radius
 * - elevation behavior
 * - light/dark mode container colors
 * - consistent Material3 styling
 *
 * This ensures that all cards throughout the template
 * follow a unified brand and elevation feel.
 */
@Composable
fun BridgeBaseCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    backgroundColor: Color? = null,   // allow override (ReflectionCard uses custom)
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable ColumnScope.() -> Unit
) {
    // ⭐ Smart default:
    //      Light Mode → normal surface
    //      Dark Mode  → slightly lighter overlay for separation
    val effectiveColor =
        backgroundColor ?: run {
            if (isDarkTheme)
                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
            else
                MaterialTheme.colorScheme.surface
        }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = effectiveColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkTheme) 8.dp else 4.dp
        )
    ) {
        Column(content = content)
    }
}
