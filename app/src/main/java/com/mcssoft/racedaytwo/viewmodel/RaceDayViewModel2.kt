package com.mcssoft.racedaytwo.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.mcssoft.racedaytwo.entity.cache.RaceMeetingCacheEntity
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import com.mcssoft.racedaytwo.repository.RaceDayRepository2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RaceDayViewModel2@ViewModelInject constructor(private var repository: RaceDayRepository2) : BaseViewModel()  {

    var raceDayCacheLiveData: LiveData<List<RaceMeetingCacheEntity>>? = null

    init {
        setMeetings()
    }

    fun setMeetings() {
        raceDayCacheLiveData = repository.fetchRaceDayList().asLiveDataViewModelScope()
    }
}

