package com.mcssoft.racedaytwo.utility

object Constants {

    // MeetingsFragment back press interval time (between presses).
    const val BACK_PRESS_TIME: Long = 2000

    // Default, or error values.
    const val NO_FILE_PATH: String = ""
    const val MINUS_ONE_L: Long = -1

    // Recyclerview.
    const val VIEW_TYPE_HEADER = 0
    const val VIEW_TYPE_DETAIL = 1

    // Meeting type filter array indexes.
    const val R_INDEX = 0
    const val MEETING_TYPE_R = "R"
    const val T_INDEX = 1
    const val MEETING_TYPE_T = "T"
    const val G_INDEX = 2
    const val MEETING_TYPE_G = "G"
    val MEETING_DEFAULT = arrayListOf(MEETING_TYPE_R, "", "")
}