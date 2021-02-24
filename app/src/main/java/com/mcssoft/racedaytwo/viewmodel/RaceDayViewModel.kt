package com.mcssoft.racedaytwo.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import com.mcssoft.racedaytwo.repository.RaceDayRepository

class RaceDayViewModel @ViewModelInject constructor(repository: RaceDayRepository)
    : BaseViewModel()  {

    var raceDayCacheLiveData = repository.fetchFromCache().asLiveDataViewModelScope()

}

