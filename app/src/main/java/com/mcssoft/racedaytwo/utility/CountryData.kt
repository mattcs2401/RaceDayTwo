package com.mcssoft.racedaytwo.utility

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.database.IFirstRunDAO
import com.mcssoft.racedaytwo.worker.FirstRunWorker
import java.util.*

/**
 * Utility class to populate "reference value" country details in the database when the database is
 * first created.
 * @param context: For resources.
 */
class CountryData(private val context: Context) {

    fun insertCountryData() {
        val workManager = WorkManager.getInstance(context)
        val key = context.resources.getString(R.string.key_file_name)
        val value = context.resources.getString(R.string.country_file)
        val workData = workDataOf(key to value)

        val firstRunWorker = OneTimeWorkRequestBuilder<FirstRunWorker>()
            .addTag("FirstRunWorker")
            .setInputData(workData)
            .build()
        workManager.enqueue(firstRunWorker)
    }

}