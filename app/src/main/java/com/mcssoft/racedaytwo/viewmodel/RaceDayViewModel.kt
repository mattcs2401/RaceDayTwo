package com.mcssoft.racedaytwo.viewmodel

import com.mcssoft.racedaytwo.repository.RaceDayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RaceDayViewModel @Inject constructor(repository: RaceDayRepository) : BaseViewModel()  {

    var raceDayCacheLiveData = repository.fetchFromCache().asLiveDataViewModelScope()

}

