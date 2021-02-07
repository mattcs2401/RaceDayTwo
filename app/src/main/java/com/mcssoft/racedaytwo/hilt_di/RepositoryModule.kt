package com.mcssoft.racedaytwo.hilt_di

import android.content.Context
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import com.mcssoft.racedaytwo.viewmodel.RaceDayViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRaceDayRepo(@ApplicationContext context: Context): RaceDayRepository {
        return RaceDayRepository(context)
    }

    @Provides
    fun provideMainViewModel(repository: RaceDayRepository): RaceDayViewModel {
        return RaceDayViewModel(repository)
    }

}