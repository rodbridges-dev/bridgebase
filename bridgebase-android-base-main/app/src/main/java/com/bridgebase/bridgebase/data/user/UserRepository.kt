package com.bridgebase.bridgebase.data.user

import com.bridgebase.bridgebase.common.FirebaseResult
import com.bridgebase.bridgebase.domain.User
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for retrieving user-related data within the application.
 *
 * This repository abstracts away the underlying data source (e.g., Firebase, REST API,
 * local database, or fake/demo source) and exposes user information as a cold [kotlinx.coroutines.flow.Flow].
 *
 * Why an interface?
 * - Promotes clean architecture by decoupling ViewModels from concrete data sources.
 * - Allows easy swapping between Fake and real implementations without changing UI code.
 * - Enables unit testing by providing mock or fake implementations.
 *
 * Why Flow?
 * - Supports asynchronous, reactive updates whenever the user data changes.
 * - Allows real backend implementations (e.g., Firestore) to push real-time updates.
 * - Keeps the API coroutine-friendly and modern.
 */
interface UserRepository {

    /**
     * Returns a [kotlinx.coroutines.flow.Flow] that emits the current user data.
     *
     * - Fake implementations typically emit once and complete.
     * - Real implementations (e.g., Firestore) may emit multiple times as data changes.
     *
     * @return A flow emitting [com.bridgebase.bridgebase.domain.User] objects representing the latest user state.
     */
    fun getUser(): Flow<FirebaseResult<User>>
}