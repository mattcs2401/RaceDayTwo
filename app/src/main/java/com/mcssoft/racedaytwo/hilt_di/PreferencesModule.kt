package com.mcssoft.racedaytwo.hilt_di

import android.content.Context
import com.mcssoft.racedaytwo.repository.RaceDayPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object PreferencesModule {

    @Singleton
    @Provides
    fun providePreferences(@ApplicationContext context: Context): RaceDayPreferences {
        return RaceDayPreferences(context)
    }

}