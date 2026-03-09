package com.bridgebase.bridgebase.common

/**
 * Standard wrapper for Firestore operations.
 *
 * This sealed class makes it easy for ViewModels and UI layers
 * to show loading, success, and error states in a type-safe way.
 *
 * Example:
 *  FirebaseResult.Loading
 *  FirebaseResult.Success(data)
 *  FirebaseResult.Error("Something went wrong")
 */
sealed class FirebaseResult<out T> {

    object Loading : FirebaseResult<Nothing>()

    data class Success<T>(val data: T) : FirebaseResult<T>()

    data class Error(val message: String, val exception: Throwable? = null) :
        FirebaseResult<Nothing>()
}