package com.mcssoft.racedaytwo.hilt_di

import android.content.Context
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import com.mcssoft.racedaytwo.viewmodel.RaceDayViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ActivityComponent::class)
object RepositoryModule {

    @Provides
    fun provideRaceDayRepo(@ApplicationContext context: Context): RaceDayRepository {
        return RaceDayRepository(context)
    }

    @Provides
    fun provideMainViewModel(repository: RaceDayRepository): RaceDayViewModel {
        return RaceDayViewModel(repository)
    }

}