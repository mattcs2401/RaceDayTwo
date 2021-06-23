package com.mcssoft.racedaytwo.utility

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.mcssoft.racedaytwo.R
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * General "utility" class fro Date/Time and file system related functions.
 */
class RaceDayUtilities @Inject constructor(private val context: Context) {
    //<editor-fold default state="collapsed" desc="Region: Date/Time">
    enum class DateFormat {
        SLASH,
        DASH
    }

    /**
     * Create the tatts.com main page Url based on today's date.
     * E.g. tatts.com/pagedata/racing/YYYY/M(M)/D(D)/RaceDay.xml
     * @param context: Used to access system string resources.
     * @return The formatted Url.
     */
    fun createRaceDayUrl(context: Context): String {
        val baseUrl = context.resources.getString(R.string.base_url)
        val datePart = getDateToday(DateFormat.SLASH)
        val mainPage = context.resources.getString(R.string.main_page)
        return "$baseUrl$datePart$mainPage"
    }

    /**
     * Get today's date in format; (1) "YYYY/MM/DD" or (2) "YYYY-MM-DD"
     * @param dateFormat: DateFormat.SLASH, or DateFormat.DASH
     * @return The formatted string.
     */
    private fun getDateToday(dateFormat: DateFormat): String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val year = calendar.get(Calendar.YEAR).toString()
        val month = ((calendar.get(Calendar.MONTH)) + 1).toString()  // Note (1) below.
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()

        return when(dateFormat) {
            DateFormat.SLASH -> "$year/$month/$day/"
            DateFormat.DASH -> "$year-$month-$day/"
        }
    }

    /**
     * Compare the Day & Month of the given time value to today's Day & Month.
     * @param timeVal: The time value to compare against.
     * @return True if the Day & Month of the given time value is equal today's Day & Month value.
     */
    private fun compareDateToToday(timeVal: Long): Boolean {
        val calendarToday = Calendar.getInstance(Locale.getDefault())
        val dayToday = calendarToday.get(Calendar.DAY_OF_MONTH).toString()
        val monthToday = ((calendarToday.get(Calendar.MONTH)) + 1).toString()  // Note (1) below.

        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = timeVal
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val month = ((calendar.get(Calendar.MONTH)) + 1).toString()  // Note (1) below.

        if((day == dayToday) && (month == monthToday)) {
            return true
        }
        return false
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: File Utils">
    /**
     * Get the path from the primary storage.
     * @return The path value (end point represents a directory), else a blank string "".
     */
    fun getPrimaryStoragePath(): String {
        var path = Constants.NO_FILE_PATH
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            path = ContextCompat.getExternalFilesDirs(context.applicationContext, null)[0].toString()
        }
        return path
    }

    /**
     * Delete all the files from the storage.
     * @param file: A File object representing the directory.
     */
    fun deleteFromStorage(file: File) {
        if(fileExists(file)) {
            file.listFiles()?.forEach { f ->
                f.delete()
            }
        }
    }

    /**
     * Simple check to see if the existing file has the same date as today.
     */
    fun dateCheck(): Boolean {
        val listing = File(getPrimaryStoragePath()).listFiles()
        return if(listing != null && listing.isNotEmpty()) {
            compareDateToToday(listing[0].lastModified())
        } else {
            false
        }
    }

    /**
     * Check if there are any files in the download directory.
     * @param file: A file object pre-set with a path (the download directory).
     * @return True if files exist in the path.
     */
    private fun fileExists(file: File): Boolean {
        val listing = file.listFiles()
        if (listing != null && listing.isNotEmpty()) {
            return true
        }
        return false
    }
    //</editor-fold>
}
/*
Notes:
(1) - https://developer.android.com/reference/java/util/Calendar:
      The first month of the year in the Gregorian and Julian calendars is JANUARY which is 0.

 */