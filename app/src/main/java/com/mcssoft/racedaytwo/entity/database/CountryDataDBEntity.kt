package com.mcssoft.racedaytwo.entity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country_details")
class CountryDataDBEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") var id: Long? = null    // value inserted by Room.

    @ColumnInfo(name = "MeetingCode") var meetingCode: String = ""
    @ColumnInfo(name = "Location") var location: String = ""
    @ColumnInfo(name = "CountryCode") var countryCode: String = ""
    @ColumnInfo(name = "Comment") var comment: String = ""

}