package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.entity.cache.RaceMeetingCacheEntity
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import kotlinx.coroutines.flow.Flow
import java.util.ArrayList
import javax.inject.Inject

//@HiltViewModel
class RaceDayViewModel @Inject constructor(private val repository: RaceDayRepository) : ViewModel() {

    /**
     *
     */
    fun initialise(lValues: ArrayList<String>) {
        lFilterValues = lValues
//        if(!repository.hasCache()) {
//            repository.createCache()
//        }
    }

    fun getFromCache(): Flow<List<RaceMeetingCacheEntity>?> =
        repository.getFromCache(lFilterValues)

    fun setTypeFilter(lValues: ArrayList<String>) {
        lFilterValues = lValues
    }

    fun createCache() = repository.createCache()

    fun clearCacheAndData() = repository.clearCacheAndData()

//    /**
//     * Get an array whose elements represent the state of the meeting type buttons of the MainFragment UI.
//     * @param lRaceType: The array representing the state.
//     * @notes [0] = meeting type "R" (Race), [1] = meeting type "T" (Trotts), [2] = meeting type "G" (Greyhound).
//     *        E.g. [0] = "R", button is checked, [0] = "", button is unchecked.
//     */
//    fun setTypeFilter(lValues: ArrayList<String>) {
//        if(lFilterValues != lValues) {
//           lFilterValues = lValues
//            repository.setTypeFilter(lFilterValues)
//        }
//    }

    private var lFilterValues = arrayListOf<String>()      // local copy.
}

