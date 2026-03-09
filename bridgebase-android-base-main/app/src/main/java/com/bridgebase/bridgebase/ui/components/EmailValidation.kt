package com.bridgebase.bridgebase.ui.components

import android.util.Patterns

/**
 * Provides reusable email validation logic for form fields.
 *
 * Can be implemented by any ViewModel or composable that needs to verify
 * email format consistency. Uses Android's built-in [Patterns.EMAIL_ADDRESS]
 * matcher for reliability.
 *
 * Example:
 * ```
 * if (emailValid(inputEmail)) { ... }
 * ```
 */
interface EmailValidation {

    /**
     * Checks whether the given [emailAddress] is valid.
     *
     * @return true if the email is non-null and matches standard format;
     * false otherwise.
     */
    fun emailValid(emailAddress: String?): Boolean {
        return if (emailAddress != null) {
            Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
        } else {
            false
        }
    }
}