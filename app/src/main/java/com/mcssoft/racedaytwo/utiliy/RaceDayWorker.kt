package com.mcssoft.racedaytwo.utiliy

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity
import com.mcssoft.racedaytwo.repository.RaceDayRepository2
import com.mcssoft.racedaytwo.retrofit.IFileDownload
import com.mcssoft.racedaytwo.retrofit.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.awaitResponse

/**
 * Utility class to do the "heavy lifting" in initialising the application's cache.
 * Note: 'cache' also means writing separate database entries.
 */
class RaceDayWorker(private val context: Context, private val params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val doWorkMsg: String
        return@withContext try {
            val retroSvc = RetrofitService(context).createService(IFileDownload::class.java)
            val fromUrl = params.inputData
                    .getString(context.resources.getString(R.string.key_url))

            val downloadFile = fromUrl?.let { retroSvc.downloadFile(it) }
            val downloadFileResponse = downloadFile?.awaitResponse()

            val success = downloadFileResponse?.isSuccessful!!
            doWorkMsg = if(success) {
                downloadFileResponse.body()?.let { cacheResponse(it) }!!
            } else {
                "Download response not successful."
            }

            if(success && (doWorkMsg == "")) {
                Result.success()
            } else {
                // Something happened, either the download response was not successful, or the cache
                // write had an issue.
                val res = workDataOf("key_result_failure" to "Result failure.", "key_msg" to doWorkMsg)
                Log.d("TAG", "workMsg: $doWorkMsg")
                Result.failure(res)
            }
        } catch (ex: Exception) {
            Log.e("TAG", "General exception: ${ex.message}")
            val res = workDataOf("key_result_failure" to "General exception: ${ex.message}")
            Result.failure(res)
        }
    }

    /**
     * Parse the response xml into meeting objects and write to database and create cache.
     */
    private fun cacheResponse(body: ResponseBody): String {
        var errMsg = ""
        try {
            val stream = body.byteStream()
            // Initialise parser.
            val raceDayParser = RaceDayParser()
            raceDayParser.setInputStream(stream)
            // Get the list of meetings.
            val meetingsListing = raceDayParser.parseForMeeting()
            // Instantiate repository (for database access).
            val raceDayRepository = RaceDayRepository2(context)

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
                // Insert an object into the cache and the database.
                raceDayRepository.insertMeeting(meeting)
            }
        } catch (ex: Exception) {
            Log.e("TAG", "Exception in cacheResponse() method" + ex.message)
            errMsg = "Exception in cacheResponse() method" + ex.message
        } finally {
            body.close()
        }
        return errMsg
    }

}