package com.mcssoft.racedaytwo.utility

import android.content.Context
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.utility.Constants.CURRENT_TIME_AFTER
import com.mcssoft.racedaytwo.utility.Constants.CURRENT_TIME_IN_WINDOW
import com.mcssoft.racedaytwo.utility.Constants.TimeUnit.HOUR
import com.mcssoft.racedaytwo.utility.Constants.TimeUnit.MINUTE
import com.mcssoft.racedaytwo.utility.Constants.THREE_MINUTES
import java.util.*
import javax.inject.Inject

/**
 * General utility class for Date/Time related methods.
 */
class DateUtilities @Inject constructor(private val context: Context) {

    //<editor-fold default state="collapsed" desc="Region: Date/Time">
    enum class DateFormat {
        SLASH,     // e.g. YYYY/MM/DD
        DASH       // " "  YYYY-MM-DD
    }

    /**
     * Create the tatts.com main page Url based on today's date.
     * E.g. tatts.com/pagedata/racing/YYYY/M(M)/D(D)/RaceDay.xml
     * @return The formatted Url.
     */
    fun createPageUrl(page: String): String {
        val baseUrl = context.resources.getString(R.string.base_url)
        val datePart = getDateToday(DateFormat.SLASH)
        return "$baseUrl$datePart$page"
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

    /**
     * Get the time in milli seconds.
     * @param hourOfDay: The hour of the day (24hr clock).
     * @param minute: The minute of the hour.
     * @return The time in milli seconds.
     */
    private fun timeToMillis(hourOfDay: Int, minute: Int) : Long {
        // Set calendar values.
        val calendar = Calendar.getInstance(Locale.getDefault()).apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        return calendar.timeInMillis
    }

    /**
     * Get the time in millis from a formatted value.
     * @param formattedTime: The time formatted as HH24:MM.
     * @return The time in milli seconds.
     * */
    fun timeToMillis(formattedTime: String): Long {
        val time = formattedTime.split(":")
        return timeToMillis(time[HOUR.ordinal].toInt(), time[MINUTE.ordinal].toInt())
    }

    /**
     * Compare the current time to that given.
     * @param givenTime: The time (i.e. Race time in mSec) to compare against the current time.
     * @return -1: The current time >= (Race time - 3) amd <= (Race time).
     *          1: The current time is after the Race time, i.e. current time > Race time.
     */
    fun compareToTime(givenTime: Long): Int {
        return when {
            isBeforeInWindow(givenTime) -> CURRENT_TIME_IN_WINDOW    // -1
            isAfter(givenTime) -> CURRENT_TIME_AFTER                 // 1
            else -> 99
        }
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
     * Is the current time within the 3 minute window before the Race time.
     * @param givenTime: The time to compare against.
     */
    private fun isBeforeInWindow(givenTime: Long) : Boolean {
        val currentTime = Calendar.getInstance(Locale.getDefault()).timeInMillis
        val windowTime = givenTime - THREE_MINUTES

        return (currentTime >= windowTime) && (currentTime <= givenTime)
    }

    /**
     * Is the current time after the given (Race) time.
     * @param givenTime: The time to compare against.
     */
    private fun isAfter(givenTime: Long) : Boolean {
        return Calendar.getInstance(Locale.getDefault()).timeInMillis > givenTime
    }

    //</editor-fold>
}
/*
Notes:
(1) - https://developer.android.com/reference/java/util/Calendar:
      The first month of the year in the Gregorian and Julian calendars is JANUARY which is 0.
 */
