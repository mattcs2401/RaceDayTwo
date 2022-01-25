package com.mcssoft.racedaytwo.utility

object Constants {

    // MeetingsFragment back press interval time (between presses).
    const val BACK_PRESS_TIME: Long = 2000

    // Recyclerview.
    enum class VIEW_TYPE(i: Int) {
        HEADER(0),
        DETAIL(1)
    }
//    const val VIEW_TYPE_HEADER = 0     // show header info for the recyclerview item.
//    const val VIEW_TYPE_DETAIL = 1     // show detail info for the recyclerview item.

    const val REQ_CODE = 0             // used by Alarm intent.

    // The current time is before the Race time in a 3 minute window,
    // i.e. current time >= (race time - 3) amd <= (Race time).
    const val CURRENT_TIME_IN_WINDOW = -1
    // The current time is after the Race time, i.e. current time > Race time.
    const val CURRENT_TIME_AFTER = 1

    const val THREE_MINUTES = 180000   // 3 minutes in milli seconds.

    // Use when splitting a string time value formatted as "HH:MI", into an array where index
    // [0]=="HH", and [1]=="MM".
    const val HOUR = 0
    const val MINUTE = 1

    // Notification.
    const val CHANNEL_ID = "99"

    // Downloader() broadcast constants.
    enum class DOWNLOAD_TYPE {
        SUCCESS_MAIN,
        SUCCESS_OTHER,
        FAILURE_MAIN,
        FAILURE_OTHER
    }

    enum class BROADCAST_TYPE {
        SUCCESS,
        ERROR
    }
}