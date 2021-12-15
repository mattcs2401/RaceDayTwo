package com.mcssoft.racedaytwo.utility

import android.content.Context
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.utility.Constants.CURRENT_TIME_AFTER
import com.mcssoft.racedaytwo.utility.Constants.CURRENT_TIME_BEFORE
import com.mcssoft.racedaytwo.utility.Constants.CURRENT_TIME_EQUAL
import com.mcssoft.racedaytwo.utility.Constants.HOUR
import com.mcssoft.racedaytwo.utility.Constants.MINUTE
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
    fun timeToMillis(hourOfDay: Int, minute: Int) : Long {
        // Set calendar values.
        val calendar = Calendar.getInstance(Locale.getDefault()).apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        return calendar.timeInMillis
    }

    fun timeToMillis(formattedTime: String): Long {
        val time = formattedTime.split(":")
        return timeToMillis(time[HOUR].toInt(), time[MINUTE].toInt())
    }

    /**
     * Get the given time value formatted as HH:MM.
     * @param givenTime: The time value in mSec.
     * @return A time value formatted as HH:MM.
     */
    fun timeFromMillis(givenTime: Long): String {
        var hour: String
        var minute: String
        Calendar.getInstance(Locale.getDefault()).apply {
            timeInMillis = givenTime
            hour = get(Calendar.HOUR_OF_DAY).toString()
            minute = get(Calendar.MINUTE).toString()
        }
        if(hour.length < 2) hour = "0$hour"
        if(minute.length < 2) minute = "0$minute"

        return "$hour:$minute"
    }

    /**
     * Compare the current time to that given.
     * @param givenTime: The time (in mSec) to compare against the current time.
     * @return -1: the current time is before that given.
     *          0: the current time is equal that given.
     *          1: the current time is after that given.
     */
    fun compareToTime(givenTime: Long) : Int {
        val calendar = Calendar.getInstance(Locale.getDefault()).apply {
            timeInMillis = givenTime
        }
        return when {
            isBefore(calendar) -> CURRENT_TIME_BEFORE
            isAfter(calendar) -> CURRENT_TIME_AFTER
            isEqual(calendar) -> CURRENT_TIME_EQUAL
            else -> {99}
        }
    }

    fun getTimeInMillis(): Long = Calendar.getInstance(Locale.getDefault()).timeInMillis

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
     * Wrapper for Calendar.isBefore()
     * @param calendar: The calendar to compare.
     */
    private fun isBefore(calendar: Calendar) : Boolean {
        return Calendar.getInstance(Locale.getDefault()).before(calendar)
    }

    /**
     * Wrapper for Calendar.isAfter()
     * @param calendar: The calendar to compare.
     */
    private fun isAfter(calendar: Calendar) : Boolean {
        return Calendar.getInstance(Locale.getDefault()).after(calendar)
    }

    /**
     * Wrapper for Calendar.isEqual()
     * @param calendar: The calendar to compare.
     */
    private fun isEqual(calendar: Calendar): Boolean {
        return Calendar.getInstance(Locale.getDefault()) == calendar
    }
    //</editor-fold>
}
/*
Notes:
(1) - https://developer.android.com/reference/java/util/Calendar:
      The first month of the year in the Gregorian and Julian calendars is JANUARY which is 0.
 */
