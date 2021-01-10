package com.mcssoft.racedaytwo.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.entity.cache.RaceMeetingCacheEntity
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
class RaceDayViewModel @ViewModelInject constructor(private var repository: RaceDayRepository) : ViewModel() {

    private val _meetings: MutableLiveData<List<RaceMeetingCacheEntity>> = MutableLiveData()

    val meetings: LiveData<List<RaceMeetingCacheEntity>>
        get() = _meetings

    fun setMeetings() {
        _meetings.value = repository.getRaceDayCache()
    }

    fun clearCache() = repository.clearCache()
}
