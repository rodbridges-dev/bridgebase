package com.bridgebase.bridgebase.data.di

import com.bridgebase.bridgebase.data.user.FakeUserRepository
import com.bridgebase.bridgebase.data.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module for binding repository interfaces
 * to their concrete implementations.
 *
 * This module enables the app to follow a clean architecture approach by ensuring
 * that ViewModels and use-cases depend only on repository *interfaces* rather than
 * concrete classes. Through @Binds, Hilt connects the [UserRepository] interface
 * to its fake implementation [FakeUserRepository] for this build.
 *
 * Benefits of this architecture:
 * - Allows the data source to be swapped easily (e.g., using a real Firebase-backed
 *   repository in production, and FakeUserRepository for offline mode or testing).
 * - Simplifies unit testing through dependency inversion.
 * - Keeps the app modular, scalable, and easier to maintain.
 *
 * This module is installed in [SingletonComponent], meaning the bound repository
 * will behave as a single instance throughout the app lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the fake user data source to the [UserRepository] interface.
     *
     * In production builds, this FakeUserRepository can be replaced with a real
     * implementation (e.g., FirestoreUserRepository) simply by updating this binding.
     *
     * @param impl The repository implementation used for user data operations.
     * @return A Hilt-managed [UserRepository] instance.
     */
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: FakeUserRepository
    ): UserRepository
}