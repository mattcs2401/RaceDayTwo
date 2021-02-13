package com.mcssoft.racedaytwo.repository

import android.app.Application
import android.content.Context
import android.util.Log
import com.mcssoft.racedaytwo.database.RaceDay
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity
import com.mcssoft.racedaytwo.utiliy.RaceDayParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * A utility class to do the 'heavy lifting' of parsing the network response xml into
 * RaceMeetingDBEntity objects and write the details to the database.
 */
class RepositoryHelper @Inject constructor(context: Context) {

    private val raceMeetingDao = RaceDay.getDatabase(context.applicationContext as Application)
            .raceDayDetailsDao()

    /**
     * Parse the network response xml into meeting objects and write to database.
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

            // Write the new details.
            for (item in meetingsListing) {
                // TODO - filter this in the parse for meeting type S.
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
                // Insert meeting object into the the database.
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
     * Insert a RaceMeeting entity.
     * @param meeting: The meeting to insert.
     */
    private fun insertMeeting(meeting: RaceMeetingDBEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            raceMeetingDao.insertMeeting(meeting)
        }
    }
}