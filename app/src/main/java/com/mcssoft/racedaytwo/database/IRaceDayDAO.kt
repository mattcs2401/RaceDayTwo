package com.mcssoft.racedaytwo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mcssoft.racedaytwo.entity.database.MeetingDBEntity
import com.mcssoft.racedaytwo.entity.database.RaceDBEntity

@Dao
interface IRaceDayDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeeting(meeting: MeetingDBEntity): Long

    @Query("delete from meeting_details")
    fun deleteAllMeetings(): Int

    @Query("select * from meeting_details")
    fun getMeetings(): List<MeetingDBEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRace(race: RaceDBEntity): Long

    @Query("delete from race_details")
    fun deleteAllRaces(): Int

    @Query("select * from race_details where mtgId = :mId")
    fun getRaces(mId: Long): List<RaceDBEntity>
}
