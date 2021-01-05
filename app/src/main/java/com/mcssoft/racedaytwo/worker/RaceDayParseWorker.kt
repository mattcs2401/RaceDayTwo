package com.mcssoft.racedaytwo.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity
import com.mcssoft.racedaytwo.events.EventResultMessage
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import com.mcssoft.racedaytwo.utiliy.Constants
import com.mcssoft.racedaytwo.utiliy.RaceDayParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.io.File

class RaceDayParseWorker (private val context: Context, private val params: WorkerParameters):
        CoroutineWorker(context, params) {

    private lateinit var raceDayParser: RaceDayParser
    private lateinit var raceDayRepository: RaceDayRepository

    // Result list of the Xml parsing.
    private var meetingsListing = ArrayList<MutableMap<String, String>>()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {

            val path = params.inputData
                .getString(context.resources.getString(R.string.key_file_path))
            val name = params.inputData
                .getString(context.resources.getString(R.string.key_file_name))

            // Initialise parser.
            raceDayParser = RaceDayParser(context)
            raceDayParser.setInputStream(File(path, name!!))

            // Get the list of meetings.
            meetingsListing = raceDayParser.parseForMeeting()

            // Instantiate repository (for database access).
            raceDayRepository = RaceDayRepository(context)

            // Write the new details.
            for (item in meetingsListing) {
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

                raceDayRepository.insertMeeting(meeting)
            }

            Log.d("TAG", "[ParseWorker] Result.success")

            // Notify in Fragment.
            EventBus.getDefault().post(EventResultMessage(Constants.PARSE_RESULT_SUCCESS))

            Result.success()

        } catch (error: Throwable) {
            // TODO - more meaningful errors, maybe a dialog ? Notify ?
            Log.e("TAG", "[ParseWorker] Result.failure. Error: " + error.message)// + error.printStackTrace())
            EventBus.getDefault().post(EventResultMessage(Constants.PARSE_RESULT_FAILURE, error.message!!))

            Result.failure()
        }
    }

}
