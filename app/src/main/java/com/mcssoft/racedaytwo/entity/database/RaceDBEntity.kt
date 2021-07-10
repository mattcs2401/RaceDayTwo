package com.mcssoft.racedaytwo.entity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "race_details")
class RaceDBEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") var id: Long? = null         // value inserted by Room.

    @ColumnInfo(name = "MtgId") var mtgId: String = ""     // foreign key for meeting_details table.

    @ColumnInfo(name = "RaceNo") var raceNo: String = ""             //
    @ColumnInfo(name = "RaceTime") var raceTime: String = ""         // YYYY-MM-DDTHH:MM:SS - Note 1.
    @ColumnInfo(name = "RaceName") var raceName: String = ""         //
    @ColumnInfo(name = "Distance") var distance: String = ""         //
    @ColumnInfo(name = "TrackDesc") var trackDesc: String = ""       // e.g. Soft
    @ColumnInfo(name = "TrackRating") var trackRating: String = ""   // e.g. 7
}
/*
Note 1:
- Need to split this string on the 'T'.
 */
