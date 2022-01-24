package com.mcssoft.racedaytwo.worker

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.database.RaceDay
import com.mcssoft.racedaytwo.entity.database.CountryDataDBEntity
import com.mcssoft.racedaytwo.utility.FirstRunParser
import com.mcssoft.racedaytwo.utility.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirstRunWorker(private val context: Context, private val params: WorkerParameters)
    : CoroutineWorker(context, params) {

    // Database access.
    private val dao = RaceDay.getDatabase(context.applicationContext as Application).firstRunDao()
    // Class that does the parsing.
    private var raceDayParser: FirstRunParser? = null
    // The master listing as returned by the parser.
    private var listing = arrayListOf<MutableMap<String, String>>()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val failureData = workDataOf()
        val failureKey = context.resources.getString(R.string.key_result_failure)
        try {
            val fileName = params.inputData.getString(context.resources.getString(R.string.key_file_name))
            // Initialise parser.
            raceDayParser = FirstRunParser(context, fileName!!)

            when(val parseDataResult = raceDayParser!!.parseForFirstRun()) {
                is DataResult.Success -> {
                    listing = parseDataResult.data
                    when(val generateDataResult = generateData(listing.size)) {
                        is DataResult.Success -> {
                            return@withContext Result.success()
                        }
                        is DataResult.Error -> {
                            // Generate listing from main page parse results failure.
                            failureData.apply { failureKey to generateDataResult.data.toString() }
                            return@withContext Result.failure(failureData)
                        }
                    }
                }
                is DataResult.Error -> {
                    failureData.apply { failureKey to parseDataResult.data.toString() }
                    return@withContext Result.failure(failureData)
                }
            }
        } catch(ex: Exception) {
            val msg = getExceptionMessage(ex)
            return@withContext Result.failure(workDataOf(failureKey to msg))
        } finally {
            raceDayParser?.closeStream()
        }
    }

    /**
     * Create the CountryData entities and populate the database.
     * @param count: The number of entities from the Xml.
     * @return A DataResult: Error returns the exception message, else Success returns blank.
     */
    private fun generateData(count: Int): DataResult<Any> {
        var ndx = 0                                        // master index var.
        var cData: Map<String, String>                     // raw country data details.
        var cEntity: CountryDataDBEntity
        val lData = arrayListOf<CountryDataDBEntity>()     // for dB write.

        try {
            while(ndx < count) {
                cData = listing[ndx]
                cEntity = collateCountryData(cData)        // create the country data entity.
                lData.add(cEntity)
                ndx += 1                                   // increment to next.
            }
            // Write to database.
            dao.insertCountryData(lData)
            val bp = "bp"

        } catch(ex: Exception) {
            return DataResult.Error(getExceptionMessage(ex))
        }
        return DataResult.Success("")
    }

    /**
     * Create the CountryData entity.
     * @param cd: The raw data from the Xml.
     * @return A CountryData entity.
     */
    private fun collateCountryData(cd: Map<String, String>): CountryDataDBEntity {
        return CountryDataDBEntity().apply {
            meetingCode = cd["MeetingCode"]!!
            location = cd["Location"]!!
            countryCode = cd["CountryCode"]!!
            comment = cd["Comment"] ?: ""
        }
    }

    /**
     * Generate error message.
     */
    private fun getExceptionMessage(ex: Exception): String {
        return StringBuilder().apply {
            append("Message: ${ex.message}")
            appendLine()
            append("Cause: ${ex.cause}")
        }.toString()
    }
}