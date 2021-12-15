package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import com.mcssoft.racedaytwo.entity.cache.RunnerCacheEntity
import com.mcssoft.racedaytwo.entity.tuples.SelectedRunner
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RunnersViewModel @Inject constructor(private val repository: RaceDayRepository) : ViewModel() {

    /**
     * Get a list of Runners for a Race.
     * @param raceId: The Race id.
     */
    fun getFromCache(raceId: Long): Flow<List<RunnerCacheEntity>?>
        = repository.getRunnersFromCache(raceId)

    /**
     * Mark the Runner as selected in the cache.
     * @param runner: Runner details (a subset of details in a tuple object).
     */
    fun setRunnerSelected(runner: SelectedRunner)
        = repository.setRunnerSelected(runner)
}

