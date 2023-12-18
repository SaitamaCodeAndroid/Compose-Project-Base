package com.season.filestorage.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FileStorageModule {

    @Provides
    @Named("CacheDir")
    @Singleton
    fun provideCacheDir(@ApplicationContext context: Context): File {
        return context.cacheDir
    }

}