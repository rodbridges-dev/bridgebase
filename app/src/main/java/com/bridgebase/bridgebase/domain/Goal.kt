package com.bridgebase.bridgebase.domain

import com.bridgebase.bridgebase.common.enums.GoalStatus

/**
 * Represents a single goal in the Goals module.
 *
 * This model is intentionally lightweight and UI-friendly:
 * - Immutable data class (ideal for Jetpack Compose state)
 * - Simple primitives for easy Firebase / REST serialization
 * - Default values allow safe construction during loading states
 */
data class GoalItem(
    // Unique identifier for the goal (Firestore doc ID or local UUID)
    val id: String = "",

    // Human-readable title the user enters (e.g., “Walk 20 minutes daily”)
    val title: String = "",

    // Normalized progress value between 0f and 1f
    // Used for progress bars and completion logic
    val progress: Float = 0f,

    // Current state of the goal (e.g., Upcoming, InProgress, Completed)
    // Enum keeps UI clean and prevents string-based errors
    val status: GoalStatus = GoalStatus.Upcoming,

    // Optional timestamp representing the user’s target completion date
    // Stored as epoch milliseconds for compatibility with Firestore/Room
    val targetDate: Long? = 0L,

    // Optional free-form notes the user can attach to a goal
    // Helpful for journaling or adding personal context
    val notes: String = "",
)


