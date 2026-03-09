package com.bridgebase.bridgebase.data.auth

import com.bridgebase.bridgebase.common.FirebaseResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * Concrete implementation of [AuthRepository] backed by Firebase Authentication.
 *
 * Handles all network-based login and password-reset actions, wrapping Firebase
 * async APIs into coroutine-friendly flows.
 */
@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {
    /** Checks if a Firebase user session currently exists. */
    override fun isLoggedIn(): Boolean = auth.currentUser != null

    /** Returns the UID of the logged-in Firebase user, if any. */
    override fun getCurrentUserId(): String? = auth.currentUser?.uid

    /**
     * Authenticates a user via Firebase email/password sign-in.
     * Emits the authenticated [FirebaseUser] or throws an exception on failure.
     */
    override fun authenticate(email: String, password: String): Flow<FirebaseResult<FirebaseUser>> = flow {
        // UI can immediately show loading
        emit(FirebaseResult.Loading)

        try {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = auth.currentUser

            if (user != null) {
                emit(FirebaseResult.Success(user))
            } else {
                emit(FirebaseResult.Error("No authenticated user returned"))
            }
        } catch (e: Exception) {
            // Allow caller to handle FirebaseAuthException or others
            emit(FirebaseResult.Error(e.message ?: "Login failed", e))
        }
    }

    /**
     * Sends a password reset email and returns a [Result] for better error handling.
     */
    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs out the current Firebase user.
     * Firebase does not provide an async logout, so no await() needed.
     */
    override suspend fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            throw e
        }
    }

}
