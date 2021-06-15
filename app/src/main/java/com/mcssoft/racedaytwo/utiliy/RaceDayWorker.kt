package com.mcssoft.racedaytwo.utiliy

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.database.RaceDay
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity
import com.mcssoft.racedaytwo.retrofit.IFileDownload
import com.mcssoft.racedaytwo.retrofit.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.awaitResponse

/**
 * Utility class to do the initialisation of the application's database entries.
 */
class RaceDayWorker(private val context: Context, private val params: WorkerParameters)
    : CoroutineWorker(context, params) {

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
                downloadFileResponse.body()?.let { writeNetworkResponse(it) }!!
            } else {
                "Download response not successful."
            }

            if(success && (doWorkMsg == "")) {
                Log.d("TAG", "doWork() - Result success :)")
                Result.success()
            } else {
                // Something happened, either the download response was not successful, or the
                // database write had an issue.
                val res = workDataOf("key_result_failure" to "doWork() - Result failure :(", "key_msg" to doWorkMsg)
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
     * Utility method to parse the network response into a set of objects and write them to the
     * database.
     * @param body The body of the network response.
     * @return On exception, a string with the error message, else an empty string.
     */
    private fun writeNetworkResponse(body: ResponseBody): String {
        var errMsg = ""
        try {
            // Database access.
            val raceMeetingDao = RaceDay.getDatabase(context.applicationContext as Application)
                .raceDayDetailsDao()
            // Initialise parser.
            val raceDayParser = RaceDayParser(context)
            raceDayParser.setInputStream(body.byteStream())
            // Get the list of meetings.
            val meetingsListing = raceDayParser.parseForMeeting()

            if(meetingsListing.size > 0) {
                // Write the new details.
                for (item in meetingsListing) {
                    // TODO - filter this in the parse for meeting type S.
                    val meeting = RaceMeetingDBEntity()
                    meeting.mtgId = item["MtgId"]!!
                    meeting.meetingCode = item["MeetingCode"]!!
                    meeting.meetingType = item["MeetingType"]!!
                    meeting.venueName = item["VenueName"]!!
                    meeting.abandoned = item["Abandoned"]!!
                    // Insert meeting object into the the database.
                    raceMeetingDao.insertMeeting(meeting)
                }
            } else {
                errMsg = "No Meeting listing. Probable parsing error."
            }
        } catch (ex: Exception) {
            errMsg = "Exception in RaceDayWorker.writeNetworkResponse(): " + ex.message
            Log.e("TAG", errMsg)
        } finally {
            body.close()
        }
        return errMsg
    }
}