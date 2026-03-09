package com.bridgebase.bridgebase.data.di

import com.bridgebase.bridgebase.data.journal.FakeJournalRepository
import com.bridgebase.bridgebase.data.journal.JournalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * JournalModule
 *
 * Hilt dependency injection module responsible for providing a single,
 * application-wide implementation of [JournalRepository].
 *
 * Why this exists:
 * - It abstracts the concrete implementation (fake vs real)
 * - Allows the ViewModel to depend on an interface, not an implementation
 * - Makes it trivial to replace FakeJournalRepository with a real
 *   Firebase/REST-backed repository in the future
 *
 * How swapping works:
 * Simply change the bound class from [FakeJournalRepository] to your real
 * repository (e.g., FirebaseJournalRepository) and the rest of the app
 * continues to work without modification.
 *
 * Example for real implementation swap:
 *
 *   @Binds
 *   abstract fun bindJournalRepository(
 *       impl: FirebaseJournalRepository
 *   ): JournalRepository
 *
 * This decoupled architecture is a core requirement for clean, testable,
 * and scalable Android production apps.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class JournalModule {

    /**
     * Bind the fake implementation to the JournalRepository interface.
     *
     * @Singleton ensures:
     * - Only one instance of the repository exists across the entire app
     * - Consistent behavior across all ViewModels
     *
     * @Binds tells Hilt:
     * "Whenever something needs a JournalRepository, provide FakeJournalRepository."
     */
    @Binds
    @Singleton
    abstract fun bindJournalRepository(
        impl: FakeJournalRepository
    ): JournalRepository
}
