package com.bridgebase.bridgebase.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * AutoFitTextOneLine
 *
 * A single-line text composable that automatically shrinks the font size
 * until the text fits within the available horizontal space. This mirrors
 * "minimumScaleFactor" behavior from UIKit/SwiftUI and is useful for dynamic
 * titles, headers, buttons, or labels that cannot wrap to multiple lines.
 *
 * The algorithm measures the text at various font sizes and uses a binary
 * search to choose the largest size that fits into the allocated width.
 *
 * @param text The string to display.
 * @param modifier Optional modifier for layout, padding, etc.
 * @param maxFontSize The preferred font size. The composable will try this
 *        size first and only scale down if necessary.
 * @param minFontSize The smallest font size allowed during auto-shrink.
 * @param color Text color.
 * @param fontWeight Optional font weight.
 * @param fontStyle Optional italic/normal style.
 * @param fontFamily Optional custom font family.
 * @param textAlign Alignment of the single-line text.
 */
@Composable
fun AutoFitTextOneLine(
    text: String,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 22.sp,   // target size
    minFontSize: TextUnit = 14.sp,   // like minimumScaleFactor(0.65)
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign = TextAlign.Start
) {
    val measurer = rememberTextMeasurer()

    // The resolved font size after measurement
    var fitted by remember(text, maxFontSize, minFontSize) { mutableStateOf(maxFontSize) }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val maxWidthPx = with(density) { maxWidth.toPx() }

        /**
         * Calculates the *natural* width of the text at a given font size,
         * ignoring layout constraints. This allows us to determine whether
         * a font size will fit inside the available width.
         */
        fun naturalWidthAt(sizeSp: Float): Float {
            val res = measurer.measure(
                text = AnnotatedString(text),
                style = TextStyle(
                    fontSize = sizeSp.sp,
                    fontWeight = fontWeight,
                    fontStyle = fontStyle,
                    fontFamily = fontFamily,
                    textAlign = textAlign
                ),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip
            )
            return res.size.width.toFloat()
        }

        // Recompute whenever text or layout constraints change
        LaunchedEffect(text, maxWidthPx, maxFontSize, minFontSize) {
            if (maxWidthPx <= 0f) return@LaunchedEffect

            var lo = minFontSize.value
            var hi = maxFontSize.value
            var best = lo

            // Binary search for largest size that fits the available width
            repeat(12) {
                val mid = (lo + hi) / 2f
                val w = naturalWidthAt(mid)
                if (w <= maxWidthPx) {
                    best = mid
                    lo = mid           // try bigger
                } else {
                    hi = mid           // too big
                }
            }
            fitted = best.sp
        }

        // Final text with the computed best-fit font size
        Text(
            text = text,
            color = color,
            fontSize = fitted,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            fontFamily = fontFamily,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign
        )
    }
}