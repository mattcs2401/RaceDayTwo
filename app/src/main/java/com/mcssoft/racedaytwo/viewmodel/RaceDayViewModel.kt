package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RaceDayViewModel @Inject constructor(repository: RaceDayRepository) : ViewModel() {

    var raceDayCache = repository.fetchFromCache()

}

