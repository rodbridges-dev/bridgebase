package com.bridgebase.bridgebase.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays motivational or informational overlay banners with gradient styling.
 *
 * Includes four [AlertType] variants — Info, Warning, Error, and Celebration —
 * each defining its own color palette and emoji. Used to surface contextual
 * feedback or success confirmations across the app.
 *
 * Example usage:
 * ```
 * WarningBanner(
 *     title = "Network Unavailable",
 *     message = "Please check your connection.",
 *     onDismiss = { ... }
 * )
 * ```
 */
enum class AlertType(val backgroundColor: Color, val textColor: Color, val emoji: String) {
    Info(Color(0xFFE3F2FD), Color(0xFF0D47A1), "💡"),
    Warning(Color(0xFFFFF4E5), Color(0xFF8B5300), "⚠️"),
    Error(Color(0xFFFFE6E6), Color(0xFFB71C1C), "❌"),
    Celebration(Color(0xFF4CAF50), Color.White, "🎉")
}

/**
 * Generic, reusable composable for showing an overlay banner.
 *
 * @param title The headline text (bold and larger font size).
 * @param message Supporting message displayed below the title.
 * @param modifier Optional [Modifier] for layout customization.
 * @param type The [AlertType] variant controlling color and icon.
 * @param onDismiss Lambda executed when user taps “Dismiss”.
 */
@Composable
fun MotivationOverlayBanner(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    type: AlertType = AlertType.Info,
    onDismiss: () -> Unit
) {
    // Define a gradient for the button background based on the alert type
    val buttonBrush = when (type) {
        AlertType.Info -> Brush.horizontalGradient(listOf(Color(0xFF42A5F5), Color(0xFF7E57C2)))
        AlertType.Warning -> Brush.horizontalGradient(listOf(Color(0xFFFFB74D), Color(0xFFF57C00)))
        AlertType.Error -> Brush.horizontalGradient(listOf(Color(0xFFE57373), Color(0xFFC62828)))
        AlertType.Celebration -> Brush.horizontalGradient(listOf(Color(0xFF66BB6A), Color(0xFF26A69A)))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .background(type.backgroundColor, shape = RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {

            // Header row with emoji + text
            Row(verticalAlignment = Alignment.Top) {
                Text(text = type.emoji, fontSize = 24.sp, modifier = Modifier.padding(horizontal = 8.dp))
                Column {
                    Text(
                        text = title,
                        color = type.textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message,
                        color = type.textColor.copy(alpha = 0.9f),
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dismiss button aligned to the end
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = null
                ) {
                    Box(
                        modifier = Modifier
                            .background(brush = buttonBrush, shape = ButtonDefaults.shape)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Dismiss", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

/** Convenience wrapper for a green celebration-style banner. */
@Composable
fun CelebrationBanner(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    MotivationOverlayBanner(
        title,
        message,
        modifier = modifier,
        type = AlertType.Celebration,
        onDismiss = onDismiss
    )
}

/** Convenience wrapper for a yellow warning-style banner. */
@Composable
fun WarningBanner(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    MotivationOverlayBanner(
        title,
        message,
        modifier = modifier,
        type = AlertType.Warning,
        onDismiss = onDismiss
    )
}