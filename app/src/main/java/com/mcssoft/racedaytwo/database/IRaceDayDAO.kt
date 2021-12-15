package com.mcssoft.racedaytwo.database

import androidx.room.*
import com.mcssoft.racedaytwo.entity.database.MeetingDBEntity
import com.mcssoft.racedaytwo.entity.database.SummaryDBEntity
import com.mcssoft.racedaytwo.entity.database.RaceDBEntity
import com.mcssoft.racedaytwo.entity.database.RunnerDBEntity
import com.mcssoft.racedaytwo.entity.tuples.MtgIdAndCode

@Dao
interface IRaceDayDAO {
/* https://blog.mindorks.com/data-access-objects-dao-in-room */

    //<editor-fold default state="collapsed" desc="Region: Meeting">
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeeting(meeting: MeetingDBEntity): Long

    @Query("delete from meeting_details")
    fun deleteAllMeetings(): Int

    @Query("select * from meeting_details")
    fun getMeetings(): List<MeetingDBEntity>

    @Query("select * from meeting_details where meetingType = :type")
    fun getMeetings(type: String): List<MeetingDBEntity>

    @Query("select _id, meetingCode from meeting_details where meetingType in (:lTypes)")
    fun getMeetingIdAndCodes(lTypes: Array<String>): List<MtgIdAndCode>

    @Query("update meeting_details set meetingDate = :rDate, meetingTime = :rTime where _id = :mId")
    fun updateMeetingDateTime(rDate: String, rTime: String, mId: Long)
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Race">
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRaces(races: List<RaceDBEntity>): Array<Long>

    @Query("delete from race_details")
    fun deleteAllRaces(): Int

    // Note: Used by RaceDayCache.createRacesCache().
    @Query("select * from race_details where mtgId = :mtgId")
    fun getRaces(mtgId: Long?): List<RaceDBEntity>

    // Note: Used in creating the RunnerDBEntity (the RaceDBEntity already exists).
    @Query("select _id from race_details where mtgCode = :mtgCode and raceNo = :raceNo")
    fun getRace(mtgCode: String, raceNo: String): Long
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Runner">
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRunners(runners: List<RunnerDBEntity>): Array<Long>

    @Query("delete from runner_details")
    fun deleteAllRunners(): Int

    @Query("select * from runner_details where raceId = :raceId")
    fun getRunners(raceId: Long): List<RunnerDBEntity>

    @Query("update runner_details set selected = :selected where _id = :id")
    fun setRunnerSelected(selected: Boolean, id: Long )
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Summary">
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSummary(entity: SummaryDBEntity): Long

    @Update
    fun updateSummary(entity: SummaryDBEntity)

    @Delete
    fun deleteSummary(entity: SummaryDBEntity)

    @Query("select * from summary_details")
    fun getAllSummaries(): List<SummaryDBEntity>

    @Query("delete from summary_details")
    fun deleteAllSummary()
    //</editor-fold>
}
