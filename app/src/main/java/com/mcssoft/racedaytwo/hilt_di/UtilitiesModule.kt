package com.mcssoft.racedaytwo.hilt_di

import android.content.Context
import com.mcssoft.racedaytwo.utiliy.RaceDayUtilities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ActivityComponent::class)
object UtilitiesModule {

    @Provides
    fun provideRaceDayUtilities(@ApplicationContext context: Context): RaceDayUtilities {
        return RaceDayUtilities()
    }
}
