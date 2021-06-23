package com.mcssoft.racedaytwo.repository

import android.app.Application
import android.content.Context
import com.mcssoft.racedaytwo.database.RaceDay
import com.mcssoft.racedaytwo.entity.cache.RaceMeetingCacheEntity
import com.mcssoft.racedaytwo.entity.mapper.RaceDayMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class RaceDayRepository @Inject constructor(context: Context) {
    // The local cache.
    private var lRaceDay: List<RaceMeetingCacheEntity>? = null

    // Coroutine scope.
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Database access.
    private val raceDetailsDAO = RaceDay.getDatabase(context.applicationContext as Application)
            .raceDayDetailsDao()

    /**
     * Essentially recreate the cache and provide that.
     */
    fun fetchFromCache() = flow {
        createCache()
        emit(lRaceDay)
    }.flowOn(Dispatchers.IO)

    /**
     * Delete all from the local cache, and database (ready to re-create).
     */
    fun clearCache() {
        lRaceDay = null
        coroutineScope.launch {
            raceDetailsDAO.deleteAll()
        }
    }

    /**
     * Create the local cache.
     * @note Meeting entities must already exist in the database.
     */
    private fun createCache(): List<RaceMeetingCacheEntity> {
        val raceDayMapper = RaceDayMapper()
        lRaceDay =  raceDayMapper.mapFromEntityList(raceDetailsDAO.getMeetings())
        return lRaceDay as List<RaceMeetingCacheEntity>
    }

}