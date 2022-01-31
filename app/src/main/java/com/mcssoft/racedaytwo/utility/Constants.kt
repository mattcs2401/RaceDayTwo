package com.mcssoft.racedaytwo.utility

object Constants {

    // MeetingsFragment back press interval time (between presses).
    const val BACK_PRESS_TIME: Long = 2000

    // Recyclerview (used for adapter's viewType parameter).
    enum class ViewType(type: Int) {
        HEADER(0),
        DETAIL(1)
    }

    const val REQ_CODE = 0             // used by Alarm intent.

    // The current time is before the Race time in a 3 minute window,
    // i.e. current time >= (race time - 3) amd <= (Race time).
    const val CURRENT_TIME_IN_WINDOW = -1
    // The current time is after the Race time, i.e. current time > Race time.
    const val CURRENT_TIME_AFTER = 1

    const val THREE_MINUTES = 180000   // 3 minutes in milli seconds.

    // Use when splitting a string time value formatted as "HH:MI", into an array where index
    // [0]=="HOUR", and [1]=="MINUTE".
    enum class TimeUnit {
        HOUR,
        MINUTE
    }

    // Notification.
    const val CHANNEL_ID = "99"

    // Downloader() broadcast constants.
    enum class DownloadType {
        SUCCESS_MAIN,        // main page download success (RaceDay.xml).
        SUCCESS_OTHER,       // other page download success, e.g. BR.xml.
        FAILURE_MAIN,        // main page download fail.
        FAILURE_OTHER        // other page download fail.
    }

    // The general type of the download broadcast.
    enum class BroadcastType {
        SUCCESS,             // successful.
        ERROR                // an error occurred.
    }

    // The general type of a worker failure.
    enum class WorkerFailure {
        MEETING,             // Meeting worker.
        RUNNER               // Runner worker.
    }

    // The general type of app startup.
    enum class START_TYPE {
        RE_START,            // just recreate caches.
        CLEAN_START          // delete and recreate everything.
    }
}