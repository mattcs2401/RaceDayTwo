package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import javax.inject.Inject

class SummaryViewModel @Inject constructor(private val repository: RaceDayRepository) : ViewModel() {

    fun getFromCache()
        = repository.getSummariesFromCacheAsFlow()

    fun getCount()
        = repository.getSummaryCount()
}