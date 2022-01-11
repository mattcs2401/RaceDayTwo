package com.mcssoft.racedaytwo.utility

object Constants {

    // MeetingsFragment back press interval time (between presses).
    const val BACK_PRESS_TIME: Long = 2000

    // Recyclerview.
    const val VIEW_TYPE_HEADER = 0     // show header info for the recyclerview item.
    const val VIEW_TYPE_DETAIL = 1     // show detail info for the recyclerview item.

    // Downloader() broadcast constants.
    const val DOWNLOAD_MAIN_FAILED = "DOWNLOAD_MAIN_FAILED"     // main page download failure.
    const val DOWNLOAD_MAIN_SUCCESS = "DOWNLOAD_MAIN_SUCCESS"   // main page download success.
    const val DOWNLOAD_OTHER_FAILED = "DOWNLOAD_OTHER_FAILED"   // other pages download failure.
    const val DOWNLOAD_OTHER_SUCCESS = "DOWNLOAD_OTHER_SUCCESS" // other pages download success.

    const val REQ_CODE = 0             // used by Alarm intent.

    // The current time is before the Race time in a 3 minute window,
    // i.e. current time >= (race time - 3) amd <= (Race time).
    const val CURRENT_TIME_IN_WINDOW = -1
    // The current time is after the Race time, i.e. current time > Race time.
    const val CURRENT_TIME_AFTER = 1
//    // The current time is < (Race time - 3).
//    const val CURRENT_TIME_BEFORE = 0

    const val THREE_MINUTES = 180000   // 3 minutes in milli seconds.

    // Use when splitting a string time value formatted as "HH:MI", into an array where index
    // [0]=="HH", and [1]=="MM".
    const val HOUR = 0
    const val MINUTE = 1

    // Notification.
    const val CHANNEL_ID = "99"
    const val NOTIFICATION_ID = 0
    const val NOTIFICATION = "NOTIFICATION"



}