package com.bridgebase.bridgebase.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridgebase.bridgebase.data.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SignupViewModel
 *
 * ViewModel responsible for handling user registration in the BridgeBase template.
 * Implements a clean MVVM architecture supported by Hilt dependency injection.
 *
 * Responsibilities:
 * - Manage input fields for name, email, and password.
 * - Validate user input in real time using a MediatorLiveData.
 * - Prevent duplicate signup submissions with an internal processing flag.
 * - Execute Firebase Authentication registration.
 * - Update the Firebase user profile with the display name.
 * - Control loading state, navigation triggers, and error dialogs exposed to the UI.
 *
 * This ViewModel pairs directly with the `SignupScreen`, providing all state and
 * business logic required for a smooth, reactive registration experience.
 */
@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    // ---------------------------------------------------------------------
    // Form State
    // ---------------------------------------------------------------------

    /** Two-way bound form fields for full name, email, and password. */
    val name = MutableLiveData("")
    val email = MutableLiveData("")
    val pass = MutableLiveData("")

    /**
     * Internal guard to prevent multiple signup requests from being initiated
     * simultaneously (e.g., double-tapping the button).
     */
    private val isProcessing = MutableLiveData(false)

    // ---------------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------------

    /**
     * Live validation using MediatorLiveData.
     *
     * Valid when:
     * - Full name is at least 2 characters.
     * - Email matches Android's email pattern.
     * - Password meets Firebase's minimum length requirement (6 chars).
     * - No signup request is currently processing.
     */
    val isValid = MediatorLiveData<Boolean>().apply {
        fun validate() {
            val n = name.value ?: ""
            val e = email.value ?: ""
            val p = pass.value ?: ""

            value = n.length >= 2 &&              // very light check
                    android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches() &&
                    p.length >= 6                 // Firebase minimum password requirement
                    && !isProcessing.value!!
        }

        addSource(name) { validate() }
        addSource(email) { validate() }
        addSource(pass) { validate() }
        addSource(isProcessing) { validate() }
    }

    // ---------------------------------------------------------------------
    // Loading & Navigation State
    // ---------------------------------------------------------------------

    /** Emits true while the signup request is being processed. */
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    /**
     * Navigation event indicating that the UI should move to the Home screen.
     * The screen observes this and resets the flag after navigating.
     */
    private val _navigateToHome = MutableStateFlow(false)
    val navigateToHome = _navigateToHome.asStateFlow()

    fun setNavigateToHome(value: Boolean) {
        _navigateToHome.value = value
    }

    // ---------------------------------------------------------------------
    // Error Dialog Handling
    // ---------------------------------------------------------------------

    /** Flag indicating whether the error banner/dialog should be shown. */
    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    /** Holds the error message shown to the user. */
    private val _dialogText = MutableLiveData("")
    val dialogText: LiveData<String> get() = _dialogText

    /** Called by UI to dismiss the visible error dialog. */
    fun onDialogDismiss() {
        _showDialog.value = false
    }

    /** Displays an error message and toggles the dialog visibility. */
    private fun showError(message: String) {
        _dialogText.value = message
        _showDialog.value = true
    }

    // ---------------------------------------------------------------------
    // Signup Logic
    // ---------------------------------------------------------------------

    /**
     * Handles Firebase Authentication registration.
     *
     * 1. Prevents duplicate submission using `isProcessing`.
     * 2. Calls Firebase to create the user.
     * 3. Optionally updates the Firebase user's display name.
     * 4. Emits navigation event on success.
     * 5. Shows an error dialog on failure.
     */
    fun signup(fullName: String, email: String, password: String) {
        isProcessing.value?.let { processing ->
            if (processing) return
            isProcessing.value = true
            _isLoading.value = true

            viewModelScope.launch {

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            // Update user display name
                            val profileUpdate = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build()

                            auth.currentUser?.updateProfile(profileUpdate)

                            _isLoading.value = false
                            isProcessing.value = false
                            _navigateToHome.value = true

                        } else {
                            // Show readable Firebase error message
                            showError(task.exception?.localizedMessage ?: "Signup failed.")
                            _isLoading.value = false
                            isProcessing.value = false
                        }
                    }
            }
        }
    }

    // ---------------------------------------------------------------------
    // Helper Setters
    // ---------------------------------------------------------------------
    fun setName(value: String) {
        name.value = value
    }

    fun setEmail(value: String) {
        email.value = value
    }

    fun setPassword(value: String) {
        pass.value = value
    }
}
