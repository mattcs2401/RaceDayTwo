package com.mcssoft.racedaytwo.entity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Class to store Runner details for the notification manager display.
 */
@Entity(tableName = "summary_details", indices = [Index(value = ["RunnerId"])])
class SummaryDBEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") var id: Long? = null                   // value inserted by Room.

    @ColumnInfo(name = "MeetingCode") var meetingCode: String = ""   //
    @ColumnInfo(name="VenueName") var venueName: String = ""         //
    @ColumnInfo(name = "RaceNo") var raceNo: String = ""             //
    @ColumnInfo(name = "RaceTime") var raceTime: String = ""         //
    @ColumnInfo(name = "RunnerId") var runnerId: Long? = null        // not displayed.
    @ColumnInfo(name = "RunnerNo") var runnerNo: String = ""         //
    @ColumnInfo(name = "RunnerName") var runnerName: String = ""     //
    @ColumnInfo(name = "Elapsed") var elapsed: Boolean = false       // Race time has elapsed ?
    @ColumnInfo(name = "Notified") var notified: Boolean = false     // Race notified.

}
