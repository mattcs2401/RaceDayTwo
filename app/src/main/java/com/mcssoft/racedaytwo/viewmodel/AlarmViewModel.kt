package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.entity.cache.SummaryCacheEntity
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import javax.inject.Inject

class AlarmViewModel @Inject constructor(private val repository: RaceDayRepository) : ViewModel() {

    fun getFromSummariesCache(elapsed: Boolean)
        = repository.getSummariesFromCache(elapsed)

    fun setElapsed(summary: SummaryCacheEntity) {
        repository.setElapsed(summary)
    }

}