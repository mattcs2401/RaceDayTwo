package com.mcssoft.racedaytwo.hilt_di

import android.content.Context
import com.mcssoft.racedaytwo.repository.RaceDayPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ActivityComponent::class)
object PreferencesModule {

    @Provides
    fun providePreferences(@ApplicationContext context: Context): RaceDayPreferences {
        return RaceDayPreferences(context)
    }

}