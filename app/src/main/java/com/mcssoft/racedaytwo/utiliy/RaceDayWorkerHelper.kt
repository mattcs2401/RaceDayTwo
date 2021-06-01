package com.mcssoft.racedaytwo.utiliy

import android.app.Application
import android.content.Context
import android.util.Log
import com.mcssoft.racedaytwo.database.RaceDay
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * A utility class to do the 'heavy lifting' of parsing the network response xml into
 * RaceMeetingDBEntity objects and write the details to the database.
 */
class RaceDayWorkerHelper @Inject constructor(private val context: Context) {

    private val raceMeetingDao = RaceDay.getDatabase(context.applicationContext as Application)
            .raceDayDetailsDao()

    /**
     * Parse the network response xml into meeting objects and write to database.
     */
    fun writeNetworkResponse(body: ResponseBody): String {
        var errMsg = ""
        try {
            // Initialise parser.
            val raceDayParser = RaceDayParser(context)
            raceDayParser.setInputStream(body.byteStream())
            // Get the list of meetings.
            val meetingsListing = raceDayParser.parseForMeeting()

            // TODO - a meetings listing size of 0 is a good indicator of a parse(ing) issue.
            Log.d("TAG", "meetings listing size: " + meetingsListing.size)

            // Write the new details.
            for (item in meetingsListing) {
                // TODO - filter this in the parse for meeting type S.
                val meeting = RaceMeetingDBEntity()
                meeting.mtgId = item["MtgId"]!!
                meeting.meetingCode = item["MeetingCode"]!!
                meeting.meetingType = item["MeetingType"]!!
                meeting.meetingName = item["MeetingName"]!!
                meeting.abandoned = item["Abandoned"]!!
                // Insert meeting object into the the database.
                insertMeeting(meeting)
            }
        } catch (ex: Exception) {
            Log.e("TAG", "Exception in writeNetworkResponse(): " + ex.message)
            errMsg = "Exception in writeNetworkResponse(): " + ex.message
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