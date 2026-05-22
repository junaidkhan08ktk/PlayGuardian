package com.playguardian.sample.di

import com.playguardian.sample.data.repository.PlayGuardianRepositoryImpl
import com.playguardian.sample.domain.repository.PlayGuardianRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlayGuardianRepository(
        impl: PlayGuardianRepositoryImpl
    ): PlayGuardianRepository
}
