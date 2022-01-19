package com.mcssoft.racedaytwo.repository

import android.content.Context
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.database.IRaceDayDAO
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.entity.cache.RaceCacheEntity
import com.mcssoft.racedaytwo.entity.cache.RunnerCacheEntity
import com.mcssoft.racedaytwo.entity.cache.SummaryCacheEntity
import com.mcssoft.racedaytwo.entity.mapper.MeetingMapper
import com.mcssoft.racedaytwo.entity.mapper.RaceMapper
import com.mcssoft.racedaytwo.entity.mapper.RunnerMapper
import com.mcssoft.racedaytwo.entity.mapper.SummaryMapper
import com.mcssoft.racedaytwo.entity.events.SelectedRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Utility class that provides the cache and some helper methods.
 * @param context: For system resources.
 */
class RaceDayCache(private val context: Context) {
    // Lists that comprise the "cache". All of type ArrayList as need to manipulate elements.
    private var lMeetings: ArrayList<MeetingCacheEntity> = arrayListOf()
    private var lRaces: ArrayList<RaceCacheEntity> = arrayListOf()
    private var lRunners: ArrayList<RunnerCacheEntity> = arrayListOf()
    private var lSummary: ArrayList<SummaryCacheEntity> = arrayListOf()

    //<editor-fold default state="collapsed" desc="Region: Meeting">
    /**
     * Create the Meetings cache.
     * @param raceDayDAO: Database access.
     * @notes: 1. Method must be called within a coroutine.
     *         2. The cache contains all Meetings regardless of type.
     */
    fun createMeetingsCache(raceDayDAO: IRaceDayDAO) {
        val meetingMapper = MeetingMapper()
        // Get the list of Meetings.
        lMeetings = meetingMapper.mapFromEntityList(raceDayDAO.getMeetings())
    }

    /**
     * Get from the Meetings cache.
     * @return A (flow) list of Meeting cache entities.
     */
    fun getMeetingsFromCache() = flow {
        emit(lMeetings)
    }.flowOn(Dispatchers.IO)

    /**
     * Get a Meeting cache entity.
     * @param mtgId: The meeting id associated with the meeting.
     * @return The Meeting cache entity.
     * @note Filer returns a list but there will only be one element.
     */
    fun getMeetingById(mtgId: Long): MeetingCacheEntity {
        return lMeetings.filter { meeting -> meeting.id == mtgId }[0]
    }

    /**
     * Delete a Meeting from the cache and database.
     * @param dao: Database access.
     * @param mce: The Meeting to delete.
     */
    fun removeMeeting(dao: IRaceDayDAO, mce: MeetingCacheEntity) {
        val meetingMapper = MeetingMapper()
        lMeetings.remove(mce)
        dao.deleteMeeting(meetingMapper.mapToMeetingEntity(mce))
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Race">
    /**
     * Create the Races cache.
     * @param dao: Database access.
     * @notes 1. Method must be called within a coroutine.
     *        2. Races cache only contains those Races associated with the given Meeting of type.
     */
    fun createRacesCache(dao: IRaceDayDAO) {
        // Get the list of Races associated with each Meeting.
        val raceMapper = RaceMapper()

        dao.getMeetings().forEach { meeting ->
            dao.getRaces(meeting.id).forEach { race ->
                lRaces.add(raceMapper.mapFromRaceEntity(race))
            }
        }
    }

    /**
     * Get Races from the cache based on the Meeting code.
     * @param mtgId: The Meeting's id.
     */
    fun getRacesByMeetingId(mtgId: Long) = flow {
        val listing = lRaces.filter { race -> race.mtgId == mtgId }
        emit(listing)
    }.flowOn(Dispatchers.IO)

    /**
     * Get a Race by its id.
     * @param id: The Race id.
     * @return A RaceCacheEntity object.
     * @note 'filter' returns a list but there should only be one entry.
     */
    private fun getRaceByRaceId(id: Long): RaceCacheEntity {
        return lRaces.filter { it.id == id }[0]
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Runner">
    /**
     * Populate the Runners cache with Runners who have the given Race id parameter.
     * @param raceDayDAO: Database access (get existing Runner data from the database).
     * @param raceId: The id of the Race.
     * @notes: 1. Method must be called within a coroutine.
     *         2. This method populates the Runners cache on an as needs basis, i.e. when the Race
     *            entry is selected in the view.
     */
    fun populateRunnersCache(raceDayDAO: IRaceDayDAO, raceId: Long) {
        val runnerMapper = RunnerMapper()
        // Update the cache.
        raceDayDAO.getRunners(raceId).forEach { dbEntity ->
            lRunners.add(runnerMapper.mapFromRunnerEntity(dbEntity))
        }
    }

    /**
     * Check that Runners with the given Race id exist in the cache.
     * @param raceId: The Race id.
     * @return True if Runners exist in the cache.
     */
    fun checkRunnersInCache(raceId: Long?): Boolean {
        return lRunners.any { runner ->
            runner.raceId == raceId
        }
    }

    /**
     * Get a list of Runner cache entities.
     * @param
     * @return A list of Runner cache entities.
     */
    fun getRunnersByRaceId(raceId: Long) = flow {
        emit(lRunners.filter { runner ->
            runner.raceId == raceId
        })
    }.flowOn(Dispatchers.IO)

    /**
     * Update the selected flag for a single RunnerCacheEntity in the cache. This method also
     * triggers the create of a Summary entry.
     * @param dao: Database access.
     * @param sr: The Runner data to draw from: meetingId, raceNo and selected flag.
     * @note Method must be called in a coroutine.
     */
    fun setRunnerSelected(dao: IRaceDayDAO, sr: SelectedRunner) {
        // Update the cache.
        val runner = lRunners.filter { runner ->
            runner.raceId == sr.raceId && runner.runnerNo == sr.runnerNo
        }[0]
        .apply { selected = sr.selected }
        // Create the Summary entry.
        if(sr.selected)
            populateSummary(dao, runner)
        else
            removeSummary(dao, runner.id!!)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: General">
    /**
     * Clear the existing caches and backing data.
     * @param raceDayDAO: Database access.
     * @notes 1. Method must be called in a coroutine.
     *        2. Destructive, deletes all database entries as well.
     */
    fun clearCachesAndData(raceDayDAO: IRaceDayDAO) {
        lMeetings = arrayListOf()
        lRaces = arrayListOf()
        lRunners = arrayListOf()
        lSummary = arrayListOf()
        raceDayDAO.deleteAllMeetings()
        raceDayDAO.deleteAllRaces()
        raceDayDAO.deleteAllRunners()
        raceDayDAO.deleteAllSummary()
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Summary">
    /**
     * Create a Summary entry.
     * @param dao: Database access.
     * @param runner: The applicable Runner entity.
     * @note Method must be called in a coroutine.
     */
    private fun populateSummary(dao: IRaceDayDAO, runner: RunnerCacheEntity) {
        val mapper = SummaryMapper()
        val sce = SummaryCacheEntity()
        // Get the Race entity.
        val rce = getRaceByRaceId(runner.raceId!!)
        // Fill in Summary entity values
        sce.meetingCode = rce.mtgCode
        sce.venueName = rce.mtgVenue
        sce.raceNo = rce.raceNo
        sce.raceTime = rce.raceTime
        sce.runnerId = runner.id
        sce.runnerNo = runner.runnerNo
        sce.runnerName = runner.runnerName
        // The default race time colour.
        sce.colour = context.resources.getColor(R.color.defaultRaceTime, null)
        // Add to Summary listing.
        lSummary.add(sce)
        // Write to database.
        dao.insertSummary(mapper.mapToSummaryEntity(sce))
    }

    /**
     * Create the Summary cache from database entries.
     * @param dao: Database access.
     * @note Method must be called in a coroutine.
     */
    fun createSummaryCache(dao: IRaceDayDAO) {
        val mapper = SummaryMapper()
        val listing = mapper.mapFromEntityList(dao.getAllSummaries())
        if(listing.isNotEmpty()) {
            listing.forEach { summary ->
                lSummary.add(summary)
            }
        }
    }

    /**
     * Get all the Summaries from the cache.
     */
    fun getSummariesFromCache() = flow {
        emit(lSummary)
    }.flowOn(Dispatchers.IO)

    fun getSummaryCount(): Int = lSummary.size

    fun getSummaryCountAsFlow() = flow {
        emit(getSummaryCount())
    }.flowOn(Dispatchers.IO)

    /**
     * Update the Summary entry to indicate that the Race time has elapsed.
     * @param dao: Database access.
     * @param sce: The Summary entity.
     */
    fun updateSummaryAsElapsed(dao: IRaceDayDAO, sce: SummaryCacheEntity) {
        val summaryMapper = SummaryMapper()
        lSummary.get(lSummary.indexOf(sce)).apply {
            elapsed = true
            colour = context.resources.getColor(R.color.pastRaceTime, null)
        }
        dao.updateSummary(summaryMapper.mapToSummaryEntity(lSummary.get(lSummary.indexOf(sce))))
    }

    /**
     * Update the Summary entry to indicate that the Race time is nearing.
     * @param dao: Database access.
     * @param sce: The Summary entity.
     */
    fun updateSummaryAsWithinWindow(dao: IRaceDayDAO, sce: SummaryCacheEntity) {
        val summaryMapper = SummaryMapper()
        lSummary.get(lSummary.indexOf(sce)).apply {
            notify = true
            colour = context.resources.getColor(R.color.nearRaceTime, null)
        }
        dao.updateSummary(summaryMapper.mapToSummaryEntity(lSummary.get(lSummary.indexOf(sce))))
    }

    /**
     * Delete a Summary from the cache and database.
     * @param dao: Database access.
     * @param sce: The Summary to delete.
     */
    fun removeSummary(dao: IRaceDayDAO, sce: SummaryCacheEntity) {
        val summaryMapper = SummaryMapper()
        lSummary.remove(sce)
        dao.deleteSummary(summaryMapper.mapToSummaryEntity(sce))
    }

    /**
     * Remove a Summary entry when the Runner entry's checkbox is un-ticked.
     * @param dao: Database access.
     * @param runnerId: The applicable Runner's id.
     * @note Method must be called in a coroutine.
     */
    private fun removeSummary(dao: IRaceDayDAO, runnerId: Long) {
        val mapper = SummaryMapper()
        val sce = getSummaryByRunnerId(runnerId)
        // Remove from cache.
        lSummary.remove(sce)
        // Remove from database.
        dao.deleteSummary(mapper.mapToSummaryEntity(sce))
    }

    /**
     * Get a Summary by its associated Runner's id.
     * @param rId: The Runner id.
     * @return A SummaryCacheEntity object.
     * @note 'filter' returns a list but there should only be one entry.
     */
    private fun getSummaryByRunnerId(rId: Long): SummaryCacheEntity {
        return lSummary.filter { it.runnerId == rId }[0]
    }
    //</editor-fold>

}