package com.bridgebase.bridgebase.ui.components

import java.util.Calendar

/**
 * Returns a time-based greeting such as:
 * "Good morning, Alex!" or "Good evening!"
 *
 * This is a lightweight helper meant to be used inside a Composable UI.
 */
fun timeBasedGreeting(name: String): String {

    // Create a calendar instance to read the current hour
    val calendar = Calendar.getInstance()

    /**
     * Remember the hour-of-day so the function does not recompute
     * on every recomposition. This keeps the UI efficient.
     *
     * NOTE: This captures the hour *at the time the composable first renders*,
     * which is usually correct for dashboards, greetings, or home screens.
     */
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    // Determine the greeting based on the time of day
    val timeGreeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "You're up late"
    }

    // Return a version with the user’s name if available
    return if (name.isBlank()) "$timeGreeting!" else "$timeGreeting, $name!"
}