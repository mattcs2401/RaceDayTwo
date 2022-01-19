package com.mcssoft.racedaytwo.entity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "runner_details", indices = [Index(value = ["RaceId", "Selected"])])
class RunnerDBEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") var id: Long? = null                   // value inserted by Room.

    // Runner details.
    // The id of the associated Race (dB row id).
    @ColumnInfo(name = "RaceId") var raceId: Long? = null

    @ColumnInfo(name = "RaceTime") var raceTime: String = ""         //
    @ColumnInfo(name = "RunnerNo") var runnerNo: String = ""         //
    @ColumnInfo(name = "RunnerName") var runnerName: String = ""     //
    @ColumnInfo(name = "Barrier") var barrier: String = ""           //
    @ColumnInfo(name = "Scratched") var scratched: String = ""       //

    // Flag to indicate Runner has been selected in the current list of Runners.
    @ColumnInfo(name = "Selected") var selected: Boolean = false
}
