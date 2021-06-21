package com.mcssoft.racedaytwo.hilt_di

import android.content.Context
import com.mcssoft.racedaytwo.utility.RaceDayUtilities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilitiesModule {

    @Singleton
    @Provides
    fun provideRaceDayUtilities(@ApplicationContext context: Context): RaceDayUtilities {
        return RaceDayUtilities(context)
    }
}
