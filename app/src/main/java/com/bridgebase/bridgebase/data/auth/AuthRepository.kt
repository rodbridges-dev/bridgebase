package com.bridgebase.bridgebase.data.auth

import com.bridgebase.bridgebase.common.FirebaseResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * Defines the core authentication contract for the app.
 *
 * Abstracts the implementation layer (Firebase, Stub, or custom) to allow
 * dependency injection and easy swapping of providers.
 */
interface AuthRepository {

    /** Returns true if a user is currently authenticated. */
    fun isLoggedIn(): Boolean

    /** Retrieves the current user ID, or null if no session exists. */
    fun getCurrentUserId(): String?

    /**
     * Attempts to authenticate with the provided [email] and [password].
     * Emits a [FirebaseUser] object upon success.
     */
    fun authenticate(email: String, password: String): Flow<FirebaseResult<FirebaseUser>>

    /**
     * Sends a password reset email to the specified [email] address.
     * Returns a [Result] indicating success or failure.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit>

    /**
     * Logs out the currently authenticated user.
     * Throws an exception if logout fails.
     */
    suspend fun logout()
}

