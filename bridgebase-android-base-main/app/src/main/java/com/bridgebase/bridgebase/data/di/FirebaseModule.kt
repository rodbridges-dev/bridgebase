package com.bridgebase.bridgebase.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module responsible for providing core Firebase singleton dependencies.
 *
 * This module exposes FirebaseAuth and FirebaseFirestore instances to the
 * dependency graph so they can be injected throughout the app (e.g., repositories,
 * data sources, or ViewModels). Centralizing Firebase initialization inside a DI
 * module ensures:
 *
 * - Clean separation between framework code and business logic.
 * - Easy mocking or replacement with Fake/Test doubles.
 * - Consistent use of the same Firebase instances across the entire app lifecycle.
 *
 * All providers are installed into [SingletonComponent], which means:
 * - The FirebaseAuth and FirebaseFirestore instances behave as global singletons.
 * - Hilt will construct them only once and reuse them for all injections.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Provides the default singleton instance of [FirebaseAuth].
     *
     * This function wraps Firebase's global accessor inside a Hilt-managed
     * dependency to make the authentication API easier to mock in tests and
     * to improve separation of concerns (ViewModels and repositories avoid
     * direct framework calls).
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Provides the default singleton instance of [FirebaseFirestore].
     *
     * Same as with FirebaseAuth, encapsulating the Firestore instance inside
     * the DI graph improves modularity and future flexibility (e.g., switching
     * environments, swapping with a FakeFirestore for local testing, or adding
     * Firestore settings in one centralized location).
     */
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // Add more as needed in the future
}
