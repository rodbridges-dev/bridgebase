package com.bridgebase.bridgebase.ui.splash

/**
 * Represents all possible UI states of the Splash screen.
 *
 * This sealed class provides a clean way for the ViewModel to signal
 * navigation outcomes or errors to the composable layer.
 *
 * Extend this with new states (e.g., MaintenanceMode) if additional
 * splash logic is introduced later.
 */
sealed class SplashUiState {

    /** While the splash animation or startup checks are running */
    data object Loading : SplashUiState()

    /** User is authenticated → navigate to Home screen */
    data object NavigateHome : SplashUiState()

    /** User is not authenticated → navigate to Login screen */
    data object NavigateLogin : SplashUiState()

    /** Optional: used if an unrecoverable error occurs (e.g., no network) */
    data class Error(val message: String) : SplashUiState()
}