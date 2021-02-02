package com.mcssoft.racedaytwo.repository

import android.app.Application
import android.content.Context
import com.mcssoft.racedaytwo.database.RaceDay
import com.mcssoft.racedaytwo.entity.cache.RaceMeetingCacheEntity
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity
import com.mcssoft.racedaytwo.entity.mapper.RaceDayMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RaceDayRepository @Inject constructor(context: Context) {
// Example - https://www.youtube.com/watch?v=CIvjwIfOG5A&ab_channel=teachmesome

    private lateinit var raceDayMapper: RaceDayMapper

    private val completableJob = Job()
    private val coroutineScope =
        CoroutineScope(Dispatchers.IO + completableJob)
    private val raceDetailsDAO = RaceDay.getDatabase(context.applicationContext as Application)
        .raceDayDetailsDao()

    private var raceDayCache: List<RaceMeetingCacheEntity>? = null

    /**
     * Create the cache that will be used by the ViewModel.
     */
    fun createCache() {
        // Only create the mapper when we need it.
        raceDayMapper = RaceDayMapper()
        coroutineScope.launch {
            raceDayCache = raceDayMapper.mapFromEntityList(raceDetailsDAO.getMeetings())
        }
    }

    fun getCache(): List<RaceMeetingCacheEntity>? {
        if (raceDayCache == null) { createCache() }
        return raceDayCache
    }

    /**
     * Insert a RaceDetails (entity) meeting.
     * @param meeting: The meeting to insert.
     */
    fun insertMeeting(meeting: RaceMeetingDBEntity) {
        coroutineScope.launch(Dispatchers.IO) {
            raceDetailsDAO.insertMeeting(meeting)
            .also {
                // TBA - refresh cache.
                raceDayCache = raceDayMapper.mapFromEntityList(raceDetailsDAO.getMeetings())
            }
        }
    }

    fun clearCache() {
        coroutineScope.launch(Dispatchers.IO) {
            raceDetailsDAO.deleteAll()
            raceDayCache = null
        }
    }

    //<editor-fold default state="collapsed" desc="Region: XXX">
    //</editor-fold>
}
/*
FYI
https://vladsonkin.com/android-coroutine-scopes-how-to-handle-a-coroutine/?utm_source=feedly&utm_medium=rss&utm_campaign=android-coroutine-scopes-how-to-handle-a-coroutine
 */
/*
    val errorHandler = CoroutineExceptionHandler { _, exception ->
      AlertDialog.Builder(this).setTitle("Error")
              .setMessage(exception.message)
              .setPositiveButton(android.R.string.ok) { _, _ -> }
              .setIcon(android.R.drawable.ic_dialog_alert).show()
    }
 */