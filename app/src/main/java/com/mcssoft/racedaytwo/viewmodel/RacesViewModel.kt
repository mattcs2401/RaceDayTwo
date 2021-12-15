package com.mcssoft.racedaytwo.viewmodel

import androidx.lifecycle.ViewModel
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.entity.cache.RaceCacheEntity
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RacesViewModel @Inject constructor(private val repository: RaceDayRepository) : ViewModel() {

    /**
     * Get a Meeting from the cache.
     * @param mtgId: The Meeting id.
     * @return The Meeting's cache entity.
     */
    fun getMeetingFromCache(mtgId: Long): MeetingCacheEntity
            = repository.getMeetingById(mtgId)

    /**
     * Get a list of Races from the Races cache.
     * @param mtgId: The Meeting id of the associated Meeting.
     * @return A (flow) list of Race cache entities.
     */
    fun getRacesByMeetingId(mtgId: Long): Flow<List<RaceCacheEntity>?>
        = repository.getRacesByMeetingId(mtgId)

    /**
     * Get a count of the Runners associated with the given Race id.
     * @param raceId: The race id.
     * @return The count value.
     */
    fun checkRunnersInCache(raceId: Long): Boolean
        = repository.checkRunnersInCache(raceId)

    fun populateRunnersCache(raceId: Long)
        = repository.populateRunnersCache(raceId)
}

