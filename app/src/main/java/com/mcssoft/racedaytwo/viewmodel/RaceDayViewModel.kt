package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import kotlinx.coroutines.flow.Flow
import java.util.ArrayList
import javax.inject.Inject

class RaceDayViewModel @Inject constructor(private val repository: RaceDayRepository) : ViewModel() {

    /**
     *
     */
    fun initialise(lValues: ArrayList<String>) {
        lFilterValues = lValues
    }

    fun getFromCache(): Flow<List<MeetingCacheEntity>?> =
        repository.getFromCache(lFilterValues)

    fun setTypeFilter(lValues: ArrayList<String>) {
        lFilterValues = lValues
    }

    fun createCache() = repository.createCache()

    fun clearCacheAndData() = repository.clearCacheAndData()

    private var lFilterValues = arrayListOf<String>()      // local copy.
}

