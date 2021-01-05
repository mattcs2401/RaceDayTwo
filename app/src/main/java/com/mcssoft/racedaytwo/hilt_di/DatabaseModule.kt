package com.mcssoft.racedaytwo.hilt_di

import android.content.Context
import androidx.room.Room
import com.mcssoft.racedaytwo.database.IRaceDayDAO
import com.mcssoft.racedaytwo.database.RaceDay
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): RaceDay {
        return Room.databaseBuilder(
            appContext,
            RaceDay::class.java,
            "race_day.db"
        ).build()
    }

    @Provides
    fun provideRaceDayDetailsDao(database: RaceDay): IRaceDayDAO {
        return database.raceDayDetailsDao()
    }

}