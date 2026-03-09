package com.bridgebase.bridgebase.domain

/**
 * Represents a single activity entry shown in the "Recent Activity" section
 * of the home dashboard.
 *
 * This is a lightweight UI-friendly model used to display quick snapshots of
 * what the user has recently done—such as adding a journal entry, updating a
 * goal, or opening the chat.
 *
 * Why a separate model?
 *  - Keeps home-screen activity lightweight and decoupled from full domain objects.
 *  - Allows fake/demo repositories to easily populate meaningful recent actions.
 *  - Real implementations can derive these items from logs, Firestore events,
 *    local analytics, or user actions.
 *
 * Fields:
 * @param title      A short label describing the action (e.g., "Journal Entry").
 * @param subtitle   A supporting description, timestamp, or progress detail
 *                   shown in smaller text below the title.
 */
data class ActivityItem(
    val title: String,
    val subtitle: String
)