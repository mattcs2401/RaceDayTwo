package com.mcssoft.racedaytwo.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mcssoft.racedaytwo.utiliy.Constants
import com.mcssoft.racedaytwo.events.EventResultMessage
import com.mcssoft.racedaytwo.retrofit.IFileDownload
import com.mcssoft.racedaytwo.retrofit.RetrofitService
import com.mcssoft.racedaytwo.utiliy.RaceDayUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import okio.Okio
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class RaceDayDownloadWorker(private val context: Context, params: WorkerParameters):
        CoroutineWorker(context, params) {

    private lateinit var raceDayUtils: RaceDayUtilities
    private lateinit var retrofitSvc: RetrofitService

    // TODO - we've downloaded the file, but still need to write to database and create cache.

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            raceDayUtils = RaceDayUtilities(context)
            retrofitSvc = RetrofitService(context)

            val retroSvc = retrofitSvc.createService(IFileDownload::class.java)
            val result = retroSvc.downloadFile(raceDayUtils.createRaceDayUrl(context))

            result?.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>?, response: Response<ResponseBody?>) {

                    if (response.isSuccessful) {
                        Log.d("TAG", "Retrofit: Response successful.")

                        // TODO - write to cache instead of file.

                        writeResponseToDisk(response.body()!!, raceDayUtils.getFullyQualifiedPath())
                    } else {
                        Log.d("TAG", "Retrofit: Response was not successful.")
                    }
                }
                override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                    Log.e("TAG", "Retrofit onFailure: Error - " + throwable.message)
                }
            })

            // Notify in Fragment.
            EventBus.getDefault().post(EventResultMessage(Constants.DOWNLOAD_RESULT_SUCCESS))

            Result.success()

        } catch (ex: Exception) {
            Log.e("TAG", "General exception: ${ex.message}")

            // Notify in Fragment.
            EventBus.getDefault().post(EventResultMessage(Constants.DOWNLOAD_RESULT_FAILURE, ex.message!!))

            Result.failure()
        }
    }

    /**
     * Write the http response to disk (with fully qualified path).
     * @param body: The Retrofit ResponseBody.
     * @param path: The fully qualified path to write to.
     * @note Must be called on a background thread otherwise get an exception.
     */
    private fun writeResponseToDisk(body: ResponseBody, path: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val bufferedSink = Okio.buffer(Okio.sink(File(path)))

            try {
                bufferedSink.writeAll(body.source())
            } catch(ex: IOException) {
                Log.e("TAG", "Write to disk exception: ${ex.message}")
            } finally {
                bufferedSink.close()
            }
        }
    }

}
