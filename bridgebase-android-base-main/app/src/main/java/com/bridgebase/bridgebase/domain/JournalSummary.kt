package com.bridgebase.bridgebase.domain

/**
 * Aggregated journal-related information surfaced on the home dashboard.
 *
 * This model represents a high-level snapshot of the user's journaling
 * behavior—ideal for driving UI widgets such as “Journal Today,” streak
 * indicators, reminders, and recent activity lists.
 *
 * Why a summary model?
 *   - The UI does not need full journal entries on the home screen.
 *   - A compact snapshot reduces data loading and simplifies ViewModel state.
 *   - Makes fake/demo implementations trivial while keeping real implementations scalable.
 *
 * Fields:
 * @param hasJournalToday  Whether the user has already submitted a journal entry today.
 * @param streakCount      Number of consecutive days the user has journaled.
 * @param lastEntryDate    Epoch millis of the most recent journal entry
 *                         (null when the user has never journaled).
 * @param message          A UI-friendly motivational string or nudge.
 * @param recentActivity   A lightweight activity log used to populate dashboard items.
 *
 * Real implementation could populate this via:
 *   - Firestore queries
 *   - Local Room database
 *   - Cached offline data
 *
 * Fake implementation can simply emit static values for template previewing.
 */
data class JournalSummary(
    val hasJournalToday: Boolean = false,
    val streakCount: Int = 0,
    val lastEntryDate: Long? = null, // epoch millis, null if none
    val message: String = "Build momentum by reflecting daily.",
    val recentActivity: List<ActivityItem> = emptyList()
)