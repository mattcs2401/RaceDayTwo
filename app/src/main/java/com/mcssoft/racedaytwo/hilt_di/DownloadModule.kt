package com.mcssoft.racedaytwo.hilt_di

import android.content.Context
import com.mcssoft.racedaytwo.utility.RaceDownloadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DownloadModule {

    @Singleton
    @Provides
    fun provideDownloadManager(@ApplicationContext context: Context): RaceDownloadManager {
        return RaceDownloadManager(context)
    }

}