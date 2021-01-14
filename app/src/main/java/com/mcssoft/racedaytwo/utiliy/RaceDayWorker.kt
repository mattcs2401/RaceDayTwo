package com.mcssoft.racedaytwo.utiliy

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity
import com.mcssoft.racedaytwo.events.EventResultMessage
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import com.mcssoft.racedaytwo.retrofit.IFileDownload
import com.mcssoft.racedaytwo.retrofit.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

/**
 * Utility class to do the "heavy lifting" in initialising the application's cache.
 * Note: 'cache' also means writing separate database entries.
 */
class RaceDayWorker(private val context: Context, private val params: WorkerParameters) : CoroutineWorker(context, params) {

    lateinit var raceDayRepository: RaceDayRepository
    lateinit var retrofitSvc: RetrofitService
    lateinit var raceDayParser: RaceDayParser

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            retrofitSvc = RetrofitService(context)

            val retroSvc = retrofitSvc.createService(IFileDownload::class.java)
            val fromUrl = params.inputData
                    .getString(context.resources.getString(R.string.key_url))
            val result = fromUrl?.let { retroSvc.downloadFile(it) }

            var success = false   // response success flag.

            result?.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>?, response: Response<ResponseBody?>) {
                    success = response.isSuccessful
                    if (success) {
                        Log.d("TAG", "Retrofit: Response successful.")
                        // Establish the cache.
                        response.body()?.let {
                            cacheResponse(it)
                            EventBus.getDefault().post(EventResultMessage(Constants.RESPONSE_RESULT_SUCCESS))
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                    Log.e("TAG", "Retrofit onFailure: Error - " + throwable.message)
                    EventBus.getDefault().post(EventResultMessage(Constants.RESPONSE_RESULT_FAILURE, "Retrofit response not successful."))
                }
            })
            // Notify the caller.
            if(success) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (ex: Exception) {
            Log.e("TAG", "General exception: ${ex.message}")
            // Notify the caller.
            EventBus.getDefault().post(EventResultMessage(Constants.RESPONSE_RESULT_FAILURE, ex.message!!))
            Result.failure()
        }
    }

    /**
     * Parse the response xml into meeting objects and write to database and create cache.
     */
    private fun cacheResponse(body: ResponseBody) {
        try {
            val stream = body.byteStream()
            // Initialise parser.
            raceDayParser = RaceDayParser()
            raceDayParser.setInputStream(stream)
            // Get the list of meetings.
            val meetingsListing = raceDayParser.parseForMeeting()
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
                // Insert an object into the cache and the database.
                raceDayRepository.insertMeeting(meeting)
            }
            EventBus.getDefault().post(EventResultMessage(Constants.PARSE_RESULT_SUCCESS))
        } catch (ex: Exception) {
            Log.e("TAG", "Exception in cacheResponse() method" + ex.message)
            EventBus.getDefault().post(EventResultMessage(Constants.PARSE_RESULT_FAILURE, ex.message!!))
        } finally {
            body.close()
        }
    }
}