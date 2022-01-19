package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.entity.cache.SummaryCacheEntity
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SummaryViewModel @Inject constructor(private val repository: RaceDayRepository) : ViewModel() {

    fun getFromCache(): Flow<List<SummaryCacheEntity>>
        = repository.getSummariesFromCache()

    fun getCountAsFlow(): Flow<Int>
            = repository.getSummaryCountAsFlow()

    fun removeSummary(sce: SummaryCacheEntity)
        = repository.removeSummary(sce)
}