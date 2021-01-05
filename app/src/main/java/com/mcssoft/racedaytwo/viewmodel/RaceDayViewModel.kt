package com.mcssoft.racedaytwo.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
class RaceDayViewModel @ViewModelInject constructor(private var repository: RaceDayRepository) : ViewModel() {
    fun clearCache() {
        TODO("Not yet implemented")
    }

    //    val meetings: LiveData<List<RaceMeeting>>?
//        get() = repository.getRaceDayCache()
//
//    fun clearCache() = repository.clearCache()
}
