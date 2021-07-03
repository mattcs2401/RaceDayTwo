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

    // Coroutine scope.
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Database access.
    private val raceDetailsDAO = RaceDay.getDatabase(context.applicationContext as Application)
        .raceDayDetailsDao()

    // The local cache.
    private var lRaceDay: List<RaceMeetingCacheEntity>? = null

    /**
     * Essentially recreate the cache and provide that.
     */
    fun getFromCache() = flow {
        emit(lRaceDay)
    }.flowOn(Dispatchers.IO)

    /**
     * Clear the existing cache and backing data.
     */
    fun clearCacheAndData() {
        lRaceDay = null
        coroutineScope.launch {
            raceDetailsDAO.deleteAll()
        }
    }

    /**
     * Create the local cache.
     * @note Meeting entities must already exist in the database.
     */
    fun createCache() {//}: List<RaceMeetingCacheEntity> {
        val raceDayMapper = RaceDayMapper()
        coroutineScope.launch {
            lRaceDay =  raceDayMapper.mapFromEntityList(raceDetailsDAO.getMeetings())
        }
//        return lRaceDay as List<RaceMeetingCacheEntity>
    }

    fun hasCache(): Boolean {
        return lRaceDay != null
    }

}