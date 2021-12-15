package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val repository: RaceDayRepository) : ViewModel() {

    fun createCaches()
        = repository.createCaches()

    fun clearCachesAndData()
        = repository.clearCachesAndData()
}

