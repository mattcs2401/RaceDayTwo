package com.mcssoft.racedaytwo.hilt_di

import android.content.Context
import com.mcssoft.racedaytwo.database.IRaceDayDAO
import com.mcssoft.racedaytwo.database.RaceDay
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import com.mcssoft.racedaytwo.repository.RaceDayRepository2
import com.mcssoft.racedaytwo.viewmodel.RaceDayViewModel
import com.mcssoft.racedaytwo.viewmodel.RaceDayViewModel2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

//    @Singleton
//    @Provides
//    fun provideRaceDayDAO(raceDay: RaceDay) : IRaceDayDAO {
//        return raceDay.raceDayDetailsDao()
//    }

    @Singleton
    @Provides
    fun provideRaceDayRepo(@ApplicationContext context: Context): RaceDayRepository {
        return RaceDayRepository(context)
    }

    @Provides
    fun provideMainViewModel(repository: RaceDayRepository): RaceDayViewModel {
        return RaceDayViewModel(repository)
    }

    @Singleton
    @Provides
    fun provideRaceDayRepo2(@ApplicationContext context: Context): RaceDayRepository2 {
        return RaceDayRepository2(context)
    }

    @Provides
    fun provideMainViewModel2(repository: RaceDayRepository2): RaceDayViewModel2 {
        return RaceDayViewModel2(repository)
    }
}