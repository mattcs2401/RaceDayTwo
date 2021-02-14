package com.mcssoft.racedaytwo.utiliy

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.retrofit.IFileDownload
import com.mcssoft.racedaytwo.retrofit.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
                val workerHelper = RaceDayWorkerHelper(context)
                downloadFileResponse.body()?.let { workerHelper.writeNetworkResponse(it) }!!
            } else {
                "Download response not successful."
            }

            if(success && (doWorkMsg == "")) {
                Result.success()
            } else {
                // Something happened, either the download response was not successful, or the
                // database write had an issue.
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
}