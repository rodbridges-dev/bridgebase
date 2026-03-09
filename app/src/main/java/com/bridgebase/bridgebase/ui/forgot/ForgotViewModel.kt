package com.bridgebase.bridgebase.ui.forgot

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridgebase.bridgebase.data.auth.AuthRepository
import com.bridgebase.bridgebase.ui.components.EmailValidation
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing password-reset requests.
 *
 * Handles user email input, validation, and calls to [AuthRepository] to send
 * a password-reset email. Displays contextual feedback messages for common
 * Firebase error types.
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(), EmailValidation {

    /** Tracks loading state for progress indicator visibility. */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** Controls visibility of alert banner and holds its text content. */
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()
    val dialogText = MutableLiveData("")

    /** Two-way bound email input field. */
    val email = MutableLiveData("")

    /** Prevents multiple requests being fired simultaneously. */
    private val isProcessing = MutableLiveData(false)

    /** Combines field and state validation for button enablement. */
    val isValid = MediatorLiveData<Boolean>().apply {
        fun combineValid(): Boolean {
            return email.value.toString().isNotEmpty() && emailValid(email.value.toString().trim())
                    && !isProcessing.value!!
        }
        addSource(email) {
            value = combineValid()
        }
        addSource(isProcessing) {
            value = combineValid()
        }
    }

    /** Updates email value. */
    fun setEmail(newEmail: String) {
        email.value = newEmail
    }

    /** Resets banner state. */
    fun onDialogDismiss() {
        _showDialog.value = false
        dialogText.value = ""
    }

    /**
     * Sends a password-reset link using [AuthRepository].
     *
     * Shows generic success message for all valid requests (Firebase’s
     * recommended pattern for Enhanced Email Privacy), while catching
     * explicit exceptions for malformed input or network issues.
     */
    fun sendResetLink(email: String) {
        isProcessing.value?.let { processing ->
            if (processing) return
        if (email.isBlank()) {
            dialogText.value = "Please enter your email."
            _showDialog.value = true
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            isProcessing.value = true
            try {
            authRepository.sendPasswordReset(email)
                dialogText.value = "If an account with that email exists, a password reset link has been sent. Please check your inbox (and spam folder)."
                _showDialog.value = true
                isProcessing.value = false
                setEmail("")
            } catch (e: Exception) {
                // Handle specific Firebase Auth exceptions where it's truly an error for the user
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // This usually means the email format is invalid.
                        dialogText.value = "The email address is not valid."
                        _showDialog.value = true
                        println("Firebase error: Invalid email format for $email. ${e.localizedMessage}")
                    }
                    // FirebaseAuthInvalidUserException might be thrown in some edge cases
                    // or if EEP is disabled, but typically sendPasswordResetEmail will
                    // succeed silently if the user doesn't exist when EEP is enabled.
                    is FirebaseAuthInvalidUserException -> {
                        dialogText.value = "If an account with that email exists, a password reset link has been sent. Please check your inbox (and spam folder)."
                        _showDialog.value = true
                        println("Firebase error: User not found for $email. Showing generic message. ${e.localizedMessage}")
                    }
                    else -> {
                        // General error catch-all (e.g., network issues)
                        dialogText.value = "An unexpected error occurred. Please try again later. (${e.localizedMessage ?: "Unknown error"})"
                        _showDialog.value = true
                        println("Firebase error: ${e.localizedMessage ?: "Unknown error"}")
                    }
                }
            } finally {
                _isLoading.value = false
                isProcessing.value = false
            }
        }
        }
    }

}