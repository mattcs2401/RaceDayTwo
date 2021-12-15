package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MeetingsViewModel @Inject constructor(private val repository: RaceDayRepository) : ViewModel() {

    fun getMeetingsFromCache(): Flow<List<MeetingCacheEntity>?>
        = repository.getMeetingsFromCache()

}

