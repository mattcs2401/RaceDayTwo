package com.mcssoft.racedaytwo.entity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "race_details", indices = [Index(value = ["MtgId","MtgCode","RaceNo"])])
class RaceDBEntity {
    // Note: Indexes are TBA.

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") var id: Long? = null    // row id value inserted by Room.

    // Meeting's (dB row) _id value.
    @ColumnInfo(name = "MtgId") var mtgId: Long? = null
    // Meeting's code and venue name. Mainly just used for display.
    @ColumnInfo(name = "MtgCode") var mtgCode: String = ""
    @ColumnInfo(name = "MtgVenue") var mtgVenue: String = ""

    // Race details.
    @ColumnInfo(name = "RaceNo") var raceNo: String = ""        //
    @ColumnInfo(name = "RaceTime") var raceTime: String = ""    // HH:MM (derived).
    @ColumnInfo(name = "RaceName") var raceName: String = ""    //
    @ColumnInfo(name = "Distance") var distance: String = ""    //
}
