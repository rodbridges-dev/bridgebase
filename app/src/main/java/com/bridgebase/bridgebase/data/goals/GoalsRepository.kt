package com.bridgebase.bridgebase.data.goals

import com.bridgebase.bridgebase.common.FirebaseResult
import com.bridgebase.bridgebase.domain.GoalItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for retrieving aggregated goal-related data.
 *
 * This abstraction isolates the UI layer from the actual data source used to
 * load goal information—whether it comes from Firebase, REST APIs, a local
 * Room database, or a fake in-memory provider.
 *
 * Architectural reasoning:
 *   - The UI consumes a simple, reactive StateFlow of [com.bridgebase.bridgebase.domain.GoalsSummary].
 *   - The ViewModel has no knowledge of Firestore snapshot listeners or network calls.
 *   - Fake implementations are trivial to provide for templates and testing.
 *
 * Why a StateFlow?
 *   - Goal counts (in-progress, completed, upcoming) are UI-critical and should
 *     always have a current value.
 *   - StateFlow ensures recomposition on updates while preserving the latest
 *     known state across configuration changes.
 *   - Real implementations may emit multiple updates over time as backend data changes.
 */
interface GoalsRepository {

    /**
     * Exposes the current goal summary (e.g., counts of in-progress, completed,
     * and upcoming goals).
     *
     * Real Repository Behavior:
     *   - Listens to live backend changes (e.g., Firestore collection snapshots).
     *   - Emits updated summaries as goal data evolves.
     *
     * Fake Repository Behavior:
     *   - Provides stable, static values ideal for offline templates and unit tests.
     *
     * @return A [kotlinx.coroutines.flow.StateFlow] emitting the latest [com.bridgebase.bridgebase.domain.GoalsSummary] data.
     */
    fun getGoalsForUser(): Flow<FirebaseResult<List<GoalItem>>>
    suspend fun addGoal(goal: GoalItem)
}