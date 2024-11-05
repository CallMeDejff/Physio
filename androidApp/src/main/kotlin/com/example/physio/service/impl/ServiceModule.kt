package com.example.physio.service.impl

import android.content.Context
import com.example.physio.service.UserPreferences
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.AuthenticationService
import com.example.physio.service.services.CacheManager
import com.example.physio.service.services.ExercisePackageService
import com.example.physio.service.services.ExerciseService
import com.example.physio.service.services.FileStorageService
import com.example.physio.service.services.ListService
import com.example.physio.service.services.StorageSampleDataService
import com.example.physio.service.services.StorageService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    abstract fun provideAuthenticationService(impl: AuthenticationServiceImpl): AuthenticationService

    @Binds
    abstract fun provideStorageService(impl: StorageServiceImpl): StorageService

    @Binds
    abstract fun provideSampleStorageDataService(impl: StorageSampleImpl): StorageSampleDataService

    @Binds
    abstract fun provideListService(impl: ListServiceImpl): ListService

    @Binds
    abstract fun provideExercisePackageService(impl: ExercisePackageServiceImpl): ExercisePackageService

    @Binds
    abstract fun provideExerciseService(impl: ExerciseServiceImpl): ExerciseService

    @Binds
    abstract fun provideFileStorageService(impl: FileStorageServiceImpl): FileStorageService

    @Binds
    abstract fun provideCacheManagerService(impl: CacheManagerImpl): CacheManager
}

@Module
@InstallIn(SingletonComponent::class)
object UserPreferencesModule {

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}