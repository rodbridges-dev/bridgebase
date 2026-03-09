package com.bridgebase.bridgebase.domain

/**
 * Represents the core user profile displayed throughout the app.
 *
 * This model lives in the `domain` layer to keep it independent of
 * UI components and data-source implementations (Firebase, REST, fake data, etc.).
 *
 * Fields:
 *  @param name       The user’s display name shown in greetings and headers.
 *  @param avatar     A drawable resource ID for the user’s profile image.
 *                    (Future real implementations may replace this with a URL.)
 *  @param tipOfDay   A short motivational message or “nudge” surfaced on the home screen.
 *
 * This model is intentionally lightweight. It can be easily extended later with
 * additional profile information (e.g., user ID, subscription status, streak data)
 * without disrupting existing UI layers.
 */
data class User(
    val name: String,
    val avatar: Int,
    val tipOfDay: String
)