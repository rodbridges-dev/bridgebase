package com.bridgebase.bridgebase.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridgebase.bridgebase.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ---------------------------------------------------------
 * SETTINGS VIEWMODEL
 * ---------------------------------------------------------
 *
 * This ViewModel is intentionally minimal — it delegates
 * all authentication work to the injected AuthRepository.
 *
 * Responsibilities:
 *  • Triggering logout
 *  • Exposing a one-time logout event to the UI using StateFlow
 *  • Keeping UI logic and business logic separated
 *
 * Why a ViewModel?
 *  - Keeps Compose screens stateless
 *  - Persists state across configuration changes
 *  - Makes the template easy to replace with Firebase/REST/Room
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * One-shot event that the UI observes.
     * When `true`, the UI should navigate to the Login screen.
     *
     * StateFlow is used instead of LiveData for:
     *  - better coroutine support
     *  - no need for lifecycle awareness in Compose
     */
    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent = _logoutEvent.asStateFlow()

    /**
     * Logs the user out using the injected AuthRepository.
     * After logout completes, an event is emitted so the UI
     * can navigate without mixing business logic into Composables.
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _logoutEvent.value = true
        }
    }
}