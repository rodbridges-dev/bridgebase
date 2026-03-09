package com.bridgebase.bridgebase.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridgebase.bridgebase.data.auth.AuthRepository
import com.bridgebase.bridgebase.utils.AppConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Splash screen.
 *
 * Responsible for running the timed splash animation and determining
 * whether the user should be navigated to the Login or Home screen.
 * Uses [com.bridgebase.bridgebase.data.auth.AuthRepository] for authentication state checks.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /** Backing state for the current splash UI state. */
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)

    /** Public immutable flow exposing the splash state to the UI layer. */
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        // Start splash logic immediately on creation
        startSplashFlow()
    }

    /**
     * Waits for the splash delay, checks login state, and emits the
     * correct [SplashUiState] to trigger navigation.
     */
    private fun startSplashFlow() {
        viewModelScope.launch {
            // Keep splash visible for configured duration
            delay(AppConfig.SPLASH_DURATION)

            // Decide navigation target based on authentication
            if (authRepository.isLoggedIn()) {
                _uiState.value = SplashUiState.NavigateHome

            } else {
                _uiState.value = SplashUiState.NavigateLogin
            }
        }
    }

}