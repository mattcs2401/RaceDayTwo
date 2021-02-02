package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

abstract class BaseViewModel : ViewModel() {
    fun <T> Flow<T>.asLiveDataViewModelScope(): LiveData<T> {
        return asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
    }
}
