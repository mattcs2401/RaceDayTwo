package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.entity.cache.SummaryCacheEntity
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import javax.inject.Inject

class AlarmViewModel @Inject constructor(private val repository: RaceDayRepository) : ViewModel() {

    /**
     * Get a listing of Summaries from the cache (as flow).
     */
    fun getFromSummariesCache()
        = repository.getSummariesFromCache()

    /**
     * Set the Summary item as being elapsed, i.e. the current time is now after the Race time.
     * @param sce: The Summary entity to update.
     */
    fun setElapsed(sce: SummaryCacheEntity) {
        repository.updateSummaryAsElapsed(sce)
    }

    /**
     * Set the Summary item to indicate near Race time.
     * @param sce: The Summary entity to update.
     */
    fun setWithinWindow(sce: SummaryCacheEntity) {
        repository.updateSummaryAsWithinWindow(sce)
    }

}