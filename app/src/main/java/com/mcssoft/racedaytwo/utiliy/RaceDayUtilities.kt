package com.mcssoft.racedaytwo.utiliy

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
     *
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
    fun getDateToday(dateFormat: DateFormat): String {
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
    fun compareDateToToday(timeVal: Long): Boolean {
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

//    /**
//     * Create the filename for the downloaded RaceDay xml data.
//     * @param context: Used to access system string resources.
//     * @return The file name as "YYYY-MM-DD_RaceDay.xml".
//     */
//    fun createRaceDayFilename(context: Context): String {
//        val datePart = getDateToday(DateFormat.DASH)
//        val under = "_"
//        val mainPage = context.resources.getString(R.string.main_page)
//        return "$datePart$under$mainPage"
//    }

//    /**
//     * Get the time from the parameter.
//     * @param timeInMillis: The time value in mSec.
//     * @return The time as HH:MM.
//     */
//    fun timeFromMillis(timeInMillis: Long): String {
//        val calendar = Calendar.getInstance(Locale.getDefault())
//        calendar.timeInMillis = timeInMillis
//        var hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
//        var minute = calendar.get(Calendar.MINUTE).toString()
//
//        if(hour.length < 2) hour = "0$hour"
//        if(minute.length < 2) minute = "0$minute"
//
//        return "$hour:$minute"
//    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: File Utils">
    /**
     * Delete all the files from the storage.
     * @param file: A File object representing the directory.
     */
    fun deleteFromStorage(file: File) {
        if(fileExists(file)) {
            val listing = file.listFiles()
            listing?.forEach { f ->
                f.delete()
            }
        }
    }

    /**
     * Check if the downloaded file has today's date (day and month).
     * @param file: The downloaded file.
     * @return True if the file day/month detail is the same as today.
     */
    fun isFileToday(file: File): Boolean =
        RaceDayUtilities(context).compareDateToToday(file.lastModified())

    /**
     * Check if there are any files in the download directory.
     * @param file: A file object pre-set with a path (the download directory).
     * @return True if files exist in the path, else false.
     */
    fun fileExists(file: File): Boolean = file.listFiles()!!.isNotEmpty()

    /**
     * Get the path from the primary storage.
     * @return The path value (end point represents a directory), else a blank string "".
     */
    fun getPrimaryStoragePath(): String {
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            return ContextCompat.getExternalFilesDirs(context.applicationContext, null)[0].toString()
        }
        return ""
    }

    /**
     * Get the fully qualified path from the primary storage.
     * @return The path value (including default filename), else a blank string "".
     */
    fun getFullyQualifiedPath(): String {
        val path = getPrimaryStoragePath()
        if(!path.equals("")) {
            return """$path${File.separator}${context.getString(R.string.main_page)}"""
        }
        return ""
    }

//    /**
//     * Write the http response to disk (with fully qualified path).
//     * @param body: The Retrofit ResponseBody.
//     * @note Must be called on a background thread otherwise get an exception.
//     */
//    fun writeResponseToDisk(body: ResponseBody) {
//
//        GlobalScope.launch(Dispatchers.IO) {
//
//            val destPath = File(getFullyQualifiedPath())
//            val bufferedSink = Okio.buffer(Okio.sink(destPath))
//
//            try {
//                bufferedSink.writeAll(body.source())
//            } catch(ex: IOException) {
//                Log.e("TAG", "Write to disk exception: ${ex.message}")
//            } finally {
//                bufferedSink.close()
//            }
//        }
//    }
    //</editor-fold>
}

/*
Notes:
(1) - https://developer.android.com/reference/java/util/Calendar:
      The first month of the year in the Gregorian and Julian calendars is JANUARY which is 0.

 */