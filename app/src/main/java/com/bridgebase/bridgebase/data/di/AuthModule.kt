package com.bridgebase.bridgebase.data.di

import com.bridgebase.bridgebase.data.auth.AuthRepository
import com.bridgebase.bridgebase.data.auth.FirebaseAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module for providing authentication-related bindings.
 *
 * This module connects the abstract [AuthRepository] interface to its concrete
 * implementation [FirebaseAuthRepository]. Using @Binds allows the app to follow
 * inversion-of-control best practices and makes the repository swappable (for example,
 * replacing FirebaseAuth with a mock implementation during testing or with another
 * provider in the future).
 *
 * - Installed in [SingletonComponent], meaning the binding lives for the entire app
 *   lifecycle (one shared instance).
 * - Uses @Binds instead of @Provides because the implementation already supports
 *   constructor injection via `@Inject constructor(...)`.
 * - Ensures clean separation of concerns between the ViewModels, use-cases, and data
 *   layer.
 *
 * This pattern is standard in modern Android apps using:
 * - MVVM architecture
 * - Clean Architecture (domain → data → presentation)
 * - Hilt for dependency injection
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    /**
     * Binds the concrete Firebase-backed authentication repository to the
     * [AuthRepository] interface.
     *
     * @param impl The implementation to use across the app.
     * @return A strongly typed [AuthRepository] instance managed by Hilt.
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepository
    ): AuthRepository
}

