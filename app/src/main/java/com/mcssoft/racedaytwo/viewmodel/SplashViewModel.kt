package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val repository: RaceDayRepository) : ViewModel() {

    /**
     * Create all the caches.
     */
    fun createCaches()
        = repository.createCaches()

    /**
     * Clear all the caches and backing data.
     */
    fun clearCachesAndData()
        = repository.clearCachesAndData()
}

