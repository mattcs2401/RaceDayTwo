package com.mcssoft.racedaytwo.utility

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.database.RaceDay
import com.mcssoft.racedaytwo.entity.database.MeetingDBEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class to:
 *  -> Parse the Meeting elements from the downloaded file.
 *  -> Create Meeting objects as an output from the parsing.
 *  -> Write them to the database.
 */
class MeetingWorker(private val context: Context, private val params: WorkerParameters)
    : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val downloadId = params.inputData.getLong(context.resources.getString(R.string.key_download_id), Constants.MINUS_ONE_L)
            // Database access.
            val raceMeetingDao = RaceDay.getDatabase(context.applicationContext as Application)
                .raceDayDetailsDao()
            // Initialise parser.
            val raceDayParser = RaceDayParser(context, downloadId)
            // Get the list of meetings.
            val meetingsListing = raceDayParser.parseForMeetings()

            if(meetingsListing.size > 0) {
                // Write the new details.
                for (item in meetingsListing) {
                    val meeting = MeetingDBEntity()
                    meeting.mtgId = item["MtgId"]!!
                    meeting.meetingCode = item["MeetingCode"]!!
                    meeting.meetingType = item["MeetingType"]!!
                    meeting.venueName = item["VenueName"]!!
                    meeting.abandoned = item["Abandoned"]!!
                    meeting.hiRaceNo = item["HiRaceNo"]!!
                    // Insert meeting object into the the database.
                    raceMeetingDao.insertMeeting(meeting)
                }
            } else {
                throw Exception("No Meeting listing. Probable parsing error.")
            }
            Result.success()
        } catch (ex: Throwable) {
            val data = workDataOf("key_result_failure" to ("""Message: ${ex.message} Cause: ${ex.cause}"""))
            Result.failure(data)
        } finally {
            // TBA
        }
    }
}
