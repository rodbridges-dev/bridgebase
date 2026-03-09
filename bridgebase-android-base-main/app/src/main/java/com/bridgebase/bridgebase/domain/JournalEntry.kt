package com.bridgebase.bridgebase.domain

/**
 * Represents a single journal entry created by the user.
 *
 * This model is intentionally simple so the template can be plugged into
 * any backend — Firebase, Supabase, REST API, or local Room database.
 *
 * @property id        Unique identifier for the entry (UUID preferred).
 * @property title     Short heading of the journal entry.
 * @property entry     Full text written by the user.
 * @property date      Unix timestamp (ms). Defaults to current time.
 * @property mood      Mood tag stored as a String (emoji or enum rawValue).
 */
data class JournalEntry(
    val id: String,
    val title: String,
    val entry: String,
    val date: Long = System.currentTimeMillis(),
    val mood: String
)