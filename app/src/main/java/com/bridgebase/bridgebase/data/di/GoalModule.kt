package com.bridgebase.bridgebase.data.di

import com.bridgebase.bridgebase.data.goals.FakeGoalRepository
import com.bridgebase.bridgebase.data.goals.GoalsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GoalModule {

    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        impl: FakeGoalRepository
    ): GoalsRepository
}
