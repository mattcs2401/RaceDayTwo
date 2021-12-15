package com.mcssoft.racedaytwo.entity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meeting_details")
class MeetingDBEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") var id: Long? = null    // value inserted by Room.

    @ColumnInfo(name = "MeetingId") var meetingId: String = ""       // e.g. 1848378112
    @ColumnInfo(name = "MeetingCode") var meetingCode: String = ""   // e.g. BR
    @ColumnInfo(name = "MeetingType") var meetingType: String = ""   // e.g. R, T, G, S
    @ColumnInfo(name = "MeetingName") var venueName: String = ""     // e.g. Sunshine Coast
    @ColumnInfo(name = "Abandoned") var abandoned: String = ""       // e.g. N or Y.
    @ColumnInfo(name = "HiRaceNo") var hiRaceNo: String = ""         // e.g. 9 (number of races)

    // Derived from Race related information.
    @ColumnInfo(name = "MeetingDate") var meetingDate: String = ""   // derived, e.g. DD-MM-YYYY.
    @ColumnInfo(name = "MeetingTime") var meetingTime: String = ""   // derived, e.g. HH:MM
    @ColumnInfo(name = "TrackDesc") var trackDesc: String = ""       // derived, e.g. Soft
    @ColumnInfo(name = "TrackCond") var trackCond: String = ""       // derived, e.g. 4
    @ColumnInfo(name = "WeatherDesc") var weatherDesc: String = ""   // derived, e.g. Overcast.
}
