package com.bridgebase.bridgebase.ui.login

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridgebase.bridgebase.common.FirebaseResult
import com.bridgebase.bridgebase.data.auth.AuthRepository
import com.bridgebase.bridgebase.ui.components.EmailValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing user authentication logic for the login screen.
 *
 * Responsibilities:
 * - Validates input fields (email, password).
 * - Interacts with [com.bridgebase.bridgebase.data.auth.AuthRepository] for login operations.
 * - Emits navigation and UI state events.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(), EmailValidation {

    /** Loading state for showing progress indicator on login button. */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** Controls whether an error dialog/banner should be shown. */
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()
    val dialogText = MutableLiveData("")

    /** Two-way bound email and password inputs. */
    val email = MutableLiveData("")
    val pass = MutableLiveData("")

    /** Internal flag preventing multiple simultaneous login attempts. */
    private val isProcessing = MutableLiveData(false)

    /** Navigation trigger to proceed to Home upon successful login. */
    private val _navigateToHome = MutableStateFlow(false)
    val navigateToHome = _navigateToHome.asStateFlow()

    fun setNavigateToHome(bool: Boolean) {
        _navigateToHome.value = bool
    }

    fun setEmail(newEmail: String) {
        email.value = newEmail
    }

    fun setPassword(newPassword: String) {
        pass.value = newPassword
    }

    /**
     * LiveData combining validation logic for both email and password fields.
     * Ensures proper formatting and minimum length requirements.
     */
    val isValid = MediatorLiveData<Boolean>().apply {
        fun combineValid(): Boolean {
            return email.value.toString().isNotEmpty() && emailValid(email.value.toString().trim())
                    && pass.value.toString().isNotEmpty() && pass.value.toString().length >= 6
                    && !isProcessing.value!!
        }
        addSource(pass) {
            value = combineValid()
        }
        addSource(email) {
            value = combineValid()
        }
        addSource(isProcessing) {
            value = combineValid()
        }
    }

    /**
     * Attempts to authenticate the user with the provided credentials.
     * Displays contextual error messages based on Firebase exception types.
     */
    fun login(email: String, password: String) {
        isProcessing.value?.let { processing ->
            if (processing) return
            isProcessing.value = true
            _isLoading.value = true
            viewModelScope.launch {
                authRepository.authenticate(email, password)
                    .flowOn(Dispatchers.IO)
                    .collect { result ->

                        when (result) {
                            is FirebaseResult.Loading -> {
                                _isLoading.value = true
                            }

                            is FirebaseResult.Success -> {
                                _isLoading.value = false
                                isProcessing.value = false
                                setNavigateToHome(true)
                            }

                            is FirebaseResult.Error -> {
                                _isLoading.value = false
                                isProcessing.value = false

                                dialogText.value = result.message
                                delay(300)
                                _showDialog.value = true
                            }
                        }
                    }

            }
        }
    }

    /** Dismisses any active dialog/banner. */
    fun onDialogDismiss() {
        _showDialog.value = false
        dialogText.value = ""
    }
}