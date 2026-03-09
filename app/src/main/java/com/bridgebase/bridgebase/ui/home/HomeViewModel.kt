package com.bridgebase.bridgebase.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridgebase.bridgebase.common.FirebaseResult
import com.bridgebase.bridgebase.data.journal.JournalRepository
import com.bridgebase.bridgebase.data.user.UserRepository
import com.bridgebase.bridgebase.domain.JournalSummary
import com.bridgebase.bridgebase.domain.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * HomeViewModel
 *
 * Exposes UI-ready observable state for the Home screen using StateFlow.
 * Responsible for:
 * - Fetching the authenticated user data
 * - Retrieving the journal summary
 * - Managing lifecycle-aware coroutine collection
 *
 * Repository interfaces allow easy swap between Fake and Firebase-backed implementations.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val journalRepository: JournalRepository
) : ViewModel() {

    /** Current logged-in user */
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    /** Summary of journal progress, streaks, and recent activity */
    private val _journalSummary = MutableStateFlow<JournalSummary?>(null)
    val journalSummary = _journalSummary.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        // Start loading data as soon as the ViewModel is created
        viewModelScope.launch {
            getUserInfo()
            getJournalSummary()
            _isLoading.value = false
        }
    }

    /**
     * Collects user info from repository and updates StateFlow.
     */
    suspend fun getUserInfo() {
        userRepository.getUser().collect { result ->
            when (result) {

                is FirebaseResult.Loading -> {
                    _isLoading.value = true
                }

                is FirebaseResult.Success -> {
                    var user = result.data

                    // Check Firebase Auth for a real name
                    val authFirstName = getAuthFirstName()
                    if (!authFirstName.isNullOrBlank()) {
                        user = user.copy(name = authFirstName)
                    }

                    _currentUser.value = user
                    _isLoading.value = false
                }

                is FirebaseResult.Error -> {
                    _currentUser.value = null
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * Collects the journal summary and exposes it to the UI.
     */
    suspend fun getJournalSummary() {
        journalRepository.getJournalSummary().collect { result ->
            when (result) {

                is FirebaseResult.Loading -> {
                    _isLoading.value = true
                }

                is FirebaseResult.Success -> {
                    val entries = result.data
                    _journalSummary.value = entries
                    _isLoading.value = false
                }

                is FirebaseResult.Error -> {
                    _journalSummary.value = null
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * Extracts the first name of the currently authenticated Firebase user.
     *
     * This helper reads the `displayName` field from FirebaseAuth's current user,
     * which is populated during signup when the profile is created.
     *
     * Behavior:
     * - Returns the user's **first name only**, even if the full name contains
     *   multiple words (e.g. "Alex Johnson" → "Alex").
     * - Returns `null` when:
     *      • No authenticated user exists
     *      • `displayName` is not set or is blank
     *
     * Why first name?
     * - Used for personalized UI elements such as "Good afternoon, Alex".
     * - Keeps greetings friendly and avoids long/hyphenated full names.
     *
     * This method allows the Home screen to dynamically personalize greetings
     * without requiring Firestore or additional profile storage.
     *
     * @return The user's first name, or `null` if unavailable.
     */
    private fun getAuthFirstName(): String? {
        val fullName = FirebaseAuth.getInstance().currentUser?.displayName ?: return null
        return fullName.trim().split(" ").firstOrNull()
    }

}