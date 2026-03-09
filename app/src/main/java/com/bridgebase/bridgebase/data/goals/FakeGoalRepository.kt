package com.bridgebase.bridgebase.data.goals

import com.bridgebase.bridgebase.common.FirebaseResult
import com.bridgebase.bridgebase.common.enums.GoalStatus
import com.bridgebase.bridgebase.domain.GoalItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeGoalRepository @Inject constructor() : GoalsRepository {

    override fun getGoalsForUser(): Flow<FirebaseResult<List<GoalItem>>> = flow {
        emit(FirebaseResult.Loading)

        delay(500) // simulate network latency

        try {
            val result = listOf(
                GoalItem(
                    id = "1",
                    title = "Read 10 minutes every morning",
                    progress = 0.4f,
                    status = GoalStatus.InProgress,
                    targetDate = System.currentTimeMillis() + (5 * 24 * 60 * 60 * 1000L),
                    notes = "Try doing this immediately after waking up."
                ),
                GoalItem(
                    id = "2",
                    title = "Walk 5,000 steps daily",
                    progress = 0.0f,
                    status = GoalStatus.Upcoming,
                    targetDate = System.currentTimeMillis() + (10 * 24 * 60 * 60 * 1000L),
                    notes = "Start with shorter walks and build up."
                ),
                GoalItem(
                    id = "3",
                    title = "Meditate 5 days a week",
                    progress = 1.0f,
                    status = GoalStatus.Completed,
                    targetDate = System.currentTimeMillis(),
                    notes = "Great work finishing this goal!"
                )
            )
            emit(FirebaseResult.Success(result))
        } catch (e: Exception) {
            emit(FirebaseResult.Error("Failed to load fake goals", e))
        }
    }

    override suspend fun addGoal(goal: GoalItem) {
        delay(300)
    }

}