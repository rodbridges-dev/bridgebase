package com.bridgebase.bridgebase.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridgebase.bridgebase.common.enums.GoalStatus
import com.bridgebase.bridgebase.data.goals.GoalsRepository
import com.bridgebase.bridgebase.domain.GoalItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import com.bridgebase.bridgebase.common.FirebaseResult

/**
 * GoalsViewModel
 *
 * Central state manager for the Goals module.
 *
 * Responsibilities:
 *  - Load user's goals from the repository
 *  - Expose UI-ready grouped lists (In Progress, Upcoming, Completed)
 *  - Maintain form state for "Add Goal" screen
 *  - Handle saving new goals (local in-memory for template demo)
 *  - Provide clean, reactive Flows for Compose UI
 *
 * This ViewModel is intentionally lightweight:
 *  Buyers can easily swap GoalsRepository for Firestore/Room/REST without
 *  changing any UI code.
 */
@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalsRepository: GoalsRepository
) : ViewModel() {

    // -------------------------------------------------------------------------
    // MASTER LIST
    // -------------------------------------------------------------------------

    /** Full list of goals returned from repository (all statuses). */
    private val _allGoals = MutableStateFlow<List<GoalItem>>(emptyList())
    val allGoals = _allGoals.asStateFlow()

    /** True while first load is running; used for shimmer placeholders. */
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // -------------------------------------------------------------------------
    // GROUPED LISTS FOR UI
    // -------------------------------------------------------------------------

    /** Goals currently active (progress < 1.0). */
    val inProgress = MutableStateFlow<List<GoalItem>>(emptyList())

    /** Goals that have not started yet. */
    val upcoming = MutableStateFlow<List<GoalItem>>(emptyList())

    /** Goals with progress = 100% / completed. */
    val completed = MutableStateFlow<List<GoalItem>>(emptyList())

    // -------------------------------------------------------------------------
    // FORM STATE (Add Goal Screen)
    // -------------------------------------------------------------------------

    /** Title text entered by user. */
    val title = MutableStateFlow("")

    /** Notes text entered by user. */
    val notes = MutableStateFlow("")

    /** Selected target date (epoch millis) or null. */
    private val _targetDate = MutableStateFlow<Long?>(null)
    val targetDate = _targetDate.asStateFlow()

    // -------------------------------------------------------------------------
    // INITIAL LOAD
    // -------------------------------------------------------------------------

    init {
        viewModelScope.launch {
            loadGoals()
        }
    }

    /**
     * Collects the Flow from repository and updates grouping lists.
     * Designed to support real sources like Firestore or Room.
     */
    private suspend fun loadGoals() {
        goalsRepository.getGoalsForUser().collect { result ->
            when (result) {

                is FirebaseResult.Loading -> {
                    _isLoading.value = true
                }

                is FirebaseResult.Success -> {
                    _isLoading.value = false
                    val goals = result.data
                    _allGoals.value = goals
                    updateSections(goals)
                }

                is FirebaseResult.Error -> {
                    _isLoading.value = false
                    // Optional: provide UI error state later
                    _allGoals.value = emptyList()
                    updateSections(emptyList())
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // GROUPING / LIST MANAGEMENT
    // -------------------------------------------------------------------------

    /**
     * Categorizes the incoming list of goals based on status.
     * This keeps UI code simple and predictable.
     */
    private fun updateSections(goals: List<GoalItem>) {
        inProgress.value = goals.filter { it.status == GoalStatus.InProgress }
        upcoming.value = goals.filter { it.status == GoalStatus.Upcoming }
        completed.value = goals.filter { it.status == GoalStatus.Completed }
    }

    // -------------------------------------------------------------------------
    // FORM UPDATES
    // -------------------------------------------------------------------------

    fun updateTitle(value: String) {
        title.value = value
    }

    fun updateNotes(value: String) {
        notes.value = value
    }

    // -------------------------------------------------------------------------
    // SAVE / CREATE GOAL
    // -------------------------------------------------------------------------

    /**
     * Creates a new GoalItem and prepends it to the list.
     *
     * NOTE:
     *  In this template version, the new goal is stored only locally.
     *  Buyers can uncomment the repository.saveGoal() call when integrating
     *  with a backend.
     */
    fun saveGoal() {
        val newGoal = GoalItem(
            id = UUID.randomUUID().toString(),
            title = title.value,
            progress = 0f,
            status = GoalStatus.Upcoming,
            targetDate = _targetDate.value,
            notes = notes.value
        )

        // Prepend to the list so newest appears first
        _allGoals.value = listOf(newGoal) + _allGoals.value
        updateSections(_allGoals.value)
    }

    // -------------------------------------------------------------------------
    // FORM RESET
    // -------------------------------------------------------------------------

    /** Clears input fields after save or when user leaves Add Goal screen. */
    fun reset() {
        title.value = ""
        notes.value = ""
        _targetDate.value = null
    }

    /**
     * Stores the UTC timestamp returned by DatePicker.
     * The UI handles formatting when displaying.
     */
    fun onDateSelected(millis: Long?) {
        // Simply store the UTC midnight timestamp provided by the DatePicker.
        _targetDate.value = millis
    }

}