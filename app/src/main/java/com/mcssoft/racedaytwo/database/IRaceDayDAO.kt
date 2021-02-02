package com.mcssoft.racedaytwo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity

@Dao
interface IRaceDayDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeeting(meeting: RaceMeetingDBEntity): Long

    @Query("delete from race_day_details")
    fun deleteAll(): Int

    @Query("select * from race_day_details")
    fun getMeetings(): List<RaceMeetingDBEntity>
}
