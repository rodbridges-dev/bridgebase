package com.bridgebase.bridgebase.data.journal

import com.bridgebase.bridgebase.common.FirebaseResult
import com.bridgebase.bridgebase.domain.JournalEntry
import com.bridgebase.bridgebase.domain.JournalSummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for fetching user journaling-related data.
 *
 * This interface abstracts the underlying data source (Firebase, REST APIs,
 * local database, or a fake/demo data provider) and exposes journal information
 * as observable reactive streams.
 *
 * Why use separate StateFlows?
 *  - Journal summary and recent activity are independent streams of data.
 *    A failure or delay in one does not block or affect the other.
 *  - This mirrors production architecture where journal stats and activity logs
 *    often come from different endpoints or Firestore collections.
 *  - ViewModels can collect these streams independently, simplifying UI logic.
 *
 * Fake vs Real Implementations:
 *  - Fake repository: emits static or simulated data useful for previews,
 *    offline templates, and unit testing.
 *  - Real repository: listens to Firestore, Room, or other sources and updates
 *    flows whenever backend data changes.
 */
interface JournalRepository {

    /**
     * Stream of high-level journaling metrics (e.g., entries this week,
     * current streak, last entry date).
     *
     * Real implementations:
     *   - May emit multiple updates as Firestore snapshots change.
     *
     * Fake implementations:
     *   - Typically expose a hardcoded or simulated summary.
     *
     * @return A [kotlinx.coroutines.flow.StateFlow] emitting the latest [com.bridgebase.bridgebase.domain.JournalSummary].
     */
    fun getJournalSummary(): Flow<FirebaseResult<JournalSummary>>

    fun getJournalEntries(): Flow<FirebaseResult<List<JournalEntry>>>

}