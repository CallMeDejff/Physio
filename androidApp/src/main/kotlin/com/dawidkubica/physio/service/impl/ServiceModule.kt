package com.dawidkubica.physio.service.impl

import android.content.Context
import com.dawidkubica.physio.service.UserPreferences
import com.dawidkubica.physio.service.services.AccountService
import com.dawidkubica.physio.service.services.AuthenticationService
import com.dawidkubica.physio.service.services.CacheManager
import com.dawidkubica.physio.service.services.ExercisePackageService
import com.dawidkubica.physio.service.services.ExerciseService
import com.dawidkubica.physio.service.services.FileStorageService
import com.dawidkubica.physio.service.services.ListService
import com.google.firebase.storage.FirebaseStorage
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
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }
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