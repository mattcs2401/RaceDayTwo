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

    // Use for time comparison.
    const val CURRENT_TIME_BEFORE = -1 // the current time is before the comparison time.
    const val CURRENT_TIME_AFTER = 1   // the current time is after the comparison time.
    const val CURRENT_TIME_EQUAL = 0   // the current time is equal the comparison time.

    // Use when splitting a string time value formatted as "HH:MI", into an array where index
    // [0]=="HH", and [1]=="MM".
    const val HOUR = 0
    const val MINUTE = 1
}