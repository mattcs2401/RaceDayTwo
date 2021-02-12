package com.mcssoft.racedaytwo.repository

import android.app.Application
import android.content.Context
import android.util.Log
import com.mcssoft.racedaytwo.database.RaceDay
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity
import com.mcssoft.racedaytwo.entity.mapper.RaceDayMapper
import com.mcssoft.racedaytwo.utiliy.RaceDayParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

class RepositoryHelper @Inject constructor(private val context: Context) {

    private val completableJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)
    private val raceDetailsDAO = RaceDay.getDatabase(context.applicationContext as Application)
        .raceDayDetailsDao()

    /**
     * Parse the response xml into meeting objects and write to database.
     */
    fun writeNetworkResponse(body: ResponseBody): String {
        var errMsg = ""
        try {
            val stream = body.byteStream()
            // Initialise parser.
            val raceDayParser = RaceDayParser()
            raceDayParser.setInputStream(stream)
            // Get the list of meetings.
            val meetingsListing = raceDayParser.parseForMeeting()
            // Instantiate repository (for database access).
            val raceDayRepository = RepositoryHelper(context)

            // Write the new details.
            for (item in meetingsListing) {
                // TODO - filter this in the parse.
                val meeting = RaceMeetingDBEntity()
                meeting.mtgId = item["MtgId"]!!
                meeting.weatherChanged = item["WeatherChanged"]!!
                meeting.meetingCode = item["MeetingCode"]!!
                meeting.venueName = item["VenueName"]!!
                meeting.hiRaceNo = item["HiRaceNo"]!!
                meeting.meetingType = item["MeetingType"]!!
                meeting.trackChanged = item["TrackChanged"]!!
                meeting.nextRaceNo = item["NextRaceNo"].toString()   // may not exist.
                meeting.sortOrder = item["SortOrder"]!!
                meeting.abandoned = item["Abandoned"]!!
                // Insert an object into the cache and the database.
                insertMeeting(meeting)
            }
        } catch (ex: Exception) {
            Log.e("TAG", "Exception in cacheResponse() method" + ex.message)
            errMsg = "Exception in cacheResponse() method" + ex.message
        } finally {
            body.close()
        }
        return errMsg
    }

    /**
     * Insert a RaceDetails (entity) meeting.
     * @param meeting: The meeting to insert.
     */
    private fun insertMeeting(meeting: RaceMeetingDBEntity) {
        coroutineScope.launch(Dispatchers.IO) {
            raceDetailsDAO.insertMeeting(meeting)
        }
    }
}