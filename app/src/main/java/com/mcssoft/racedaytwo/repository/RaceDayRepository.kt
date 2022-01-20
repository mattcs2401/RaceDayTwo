package com.mcssoft.racedaytwo.repository

import android.app.Application
import android.content.Context
import com.mcssoft.racedaytwo.database.RaceDay
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.entity.cache.SummaryCacheEntity
import com.mcssoft.racedaytwo.entity.events.SelectedRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RaceDayRepository @Inject constructor(context: Context) {

    // Coroutine scope.
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Database access.
    private val raceDayDAO =
        RaceDay.getDatabase(context.applicationContext as Application).raceDayDao()

    // Main cache.
    private var raceDayCache = RaceDayCache(context)

    //<editor-fold default state="collapsed" desc="Region: Meeting">
    /**
     * Create the local caches.
     */
    fun createCaches() {
        coroutineScope.launch {
            createMeetingsCache()
            createRacesCache()
            createSummaryCache()
        }
        Thread.sleep(100) // TBA, give time to create caches ?
    }

    /**
     * Get a Meeting from the cache.
     * @param mtgId: The Meeting's id.
     */
    fun getMeetingById(mtgId: Long) = raceDayCache.getMeetingById(mtgId)

    /**
     *
     */
    fun getMeetingsFromCache() = raceDayCache.getMeetingsFromCache()

    /**
     * Remove a Meeting from the cache and database.
     * @param mce: The Meeting to remove.
     */
    fun removeMeeting(mce: MeetingCacheEntity) {
        coroutineScope.launch {
            raceDayCache.removeMeeting(raceDayDAO, mce)
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Race">
    /**
     * Get a list of Races from the cache.
     * @param mtgId: The Meeting id associated with each Race.
     */
    fun getRacesByMeetingId(mtgId: Long) = raceDayCache.getRacesByMeetingId(mtgId)
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Runner">
    fun checkRunnersInCache(raceId: Long): Boolean = raceDayCache.checkRunnersInCache(raceId)

    /**
     * Populate some elements of the Runners cache.
     * @param raceId: The Race id (of the Race associated with the Runners).
     */
    fun populateRunnersCache(raceId: Long) {
        coroutineScope.launch {
            raceDayCache.populateRunnersCache(raceDayDAO, raceId)
        }
    }

    /**
     * Get a list of Runners from the cache associated with a Race.
     * @param raceId: The Race id.
     */
    fun getRunnersFromCache(raceId: Long) = raceDayCache.getRunnersByRaceId(raceId)

    /**
     * Set a Runner as having been selected for the schedule.
     * @param sr: The SelectedRunner object that holds required information.
     */
    fun setRunnerSelected(sr: SelectedRunner) {
        coroutineScope.launch {
            raceDayCache.setRunnerSelected(raceDayDAO, sr)
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Summary">
    fun getSummariesFromCache()
        = raceDayCache.getSummariesFromCache()

    fun getSummaryCountAsFlow(): Flow<Int> = raceDayCache.getSummaryCountAsFlow()

    /**
     * Update the Summary entry to indicate that the Race time has elapsed.
     * @param sce: The Summary entity.
     */
    fun updateSummaryAsElapsed(sce: SummaryCacheEntity) {
        coroutineScope.launch {
            raceDayCache.updateSummaryAsElapsed(raceDayDAO, sce)
        }
    }

    /**
     * Update the Summary entry to indicate that the Race time is nearing.
     * @param sce: The Summary entity.
     */
    fun updateSummaryAsWithinWindow(sce: SummaryCacheEntity) {
        coroutineScope.launch {
            raceDayCache.updateSummaryAsWithinWindow(raceDayDAO, sce)
        }
    }

    /**
     * Remove a Meeting from the cache and database.
     * @param sce: The Summary to remove.
     */
    fun removeSummary(sce: SummaryCacheEntity) {
        coroutineScope.launch {
            raceDayCache.removeSummary(raceDayDAO, sce)
        }
    }
    //</editor-fold>

    /**
     * Clear the existing caches and backing data.
     */
    fun clearCachesAndData() {
        coroutineScope.launch {
            raceDayCache.clearCachesAndData(raceDayDAO)
        }
    }

    private fun createMeetingsCache()
        = raceDayCache.createMeetingsCache(raceDayDAO)

    private fun createRacesCache()
        = raceDayCache.createRacesCache(raceDayDAO)

    private fun createSummaryCache()
            = raceDayCache.createSummaryCache(raceDayDAO)

}