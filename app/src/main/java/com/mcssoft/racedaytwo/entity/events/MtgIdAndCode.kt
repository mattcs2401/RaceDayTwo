package com.mcssoft.racedaytwo.entity.events

import androidx.room.ColumnInfo

/**
 * Class that models the return type for (select statement) IRaceDayDAO.getMeetingIdAndCodes().
 */
data class MtgIdAndCode(@ColumnInfo(name = "_id") var mtgId: Long? = null,
                        @ColumnInfo(name = "MeetingCode") var mtgCode: String = "")