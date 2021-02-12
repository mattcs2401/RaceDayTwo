package com.mcssoft.racedaytwo.repository

import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import com.mcssoft.racedaytwo.database.RaceDay
import com.mcssoft.racedaytwo.entity.cache.RaceMeetingCacheEntity
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity
import com.mcssoft.racedaytwo.entity.mapper.RaceDayMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class RaceDayRepository @Inject constructor(context: Context) {

    private var lRaceDay: List<RaceMeetingCacheEntity>? = null

    private val completableJob = Job()
    private lateinit var raceDayMapper: RaceDayMapper
    private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)
    private val raceDetailsDAO = RaceDay.getDatabase(context.applicationContext as Application)
            .raceDayDetailsDao()

    @WorkerThread
    fun fetchRaceDayList() = flow {
        if (lRaceDay == null || lRaceDay!!.isEmpty()) {
            createCache()
        }
        emit(lRaceDay)
    }.flowOn(Dispatchers.IO)

//    /**
//     * Insert a RaceDetails (entity) meeting.
//     * @param meeting: The meeting to insert.
//     */
//    fun insertMeeting(meeting: RaceMeetingDBEntity) {
//        coroutineScope.launch(Dispatchers.IO) {
//            raceDetailsDAO.insertMeeting(meeting)
//        }
//    }

    fun createCache(): List<RaceMeetingCacheEntity> {
        raceDayMapper = RaceDayMapper()
        lRaceDay =  raceDayMapper.mapFromEntityList(raceDetailsDAO.getMeetings())
        return lRaceDay as List<RaceMeetingCacheEntity>
    }

    fun clearCache() {
        coroutineScope.launch(Dispatchers.IO) {
            raceDetailsDAO.deleteAll()
        }
    }
}