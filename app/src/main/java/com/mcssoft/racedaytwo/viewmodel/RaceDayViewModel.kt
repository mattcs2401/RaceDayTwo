package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.entity.cache.RaceMeetingCacheEntity
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RaceDayViewModel @Inject constructor(val repository: RaceDayRepository) : ViewModel() {

    /**
     *
     */
    fun initialise() {
        // TBA - apply the filter.
//        if(!repository.hasCache()) {
//            repository.createCache()
//        }
    }

    fun getFromCache(): Flow<List<RaceMeetingCacheEntity>?> {
        return repository.getFromCache()
    }
}

