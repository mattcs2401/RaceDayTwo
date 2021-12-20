package com.mcssoft.racedaytwo.worker

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.database.RaceDay
import com.mcssoft.racedaytwo.entity.database.MeetingDBEntity
import com.mcssoft.racedaytwo.entity.database.RaceDBEntity
import com.mcssoft.racedaytwo.utility.DataResult
import com.mcssoft.racedaytwo.utility.RaceDayParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class to:
 *  -> Parse the Meeting and Race elements from the downloaded file (xml).
 *  -> Create Meeting and Race objects as an output from the parsing and write them to the database.
 *  @param context: For system resources and used by RaceDayParser().
 *  @param params: The filename of the file to parse. Used by RaceDayParser().
 */
class MeetingWorker(private val context: Context, private val params: WorkerParameters)
    : CoroutineWorker(context, params) {
    // TODO - error trapping needs to be a lot better.
    // Database access.
    private val raceMeetingDao = RaceDay.getDatabase(context.applicationContext as Application)
          .raceDayDao()
    // Class that does the parsing.
    private var raceDayParser: RaceDayParser? = null
    // The master listing as returned by the parser.
    private var listing = arrayListOf<MutableMap<String, String>>()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val failureKey = context.resources.getString(R.string.key_result_failure)
        try {
            val fileName = params.inputData.getString(context.resources.getString(R.string.key_file_name))
            // Initialise parser.
            raceDayParser = RaceDayParser(context, fileName!!)
            // Get the list of meetings.
            when(val parseDataResult = raceDayParser!!.parseForMeetingsAndRaces()) {
                is DataResult.Success -> {
                    // Main page parsing success.
                    listing = parseDataResult.data
                    // Write the Meeting and Race info to the database.
                    when(val generateDataResult = generateData(listing.size)) {
                        is DataResult.Success -> { return@withContext Result.success() }
                        is DataResult.Error -> {
                            // Generate listing from main page parse results failure.
                            val failureData = workDataOf(failureKey to generateDataResult.data.toString())
                            return@withContext Result.failure(failureData)
                        }
                    }
                }
                is DataResult.Error -> {
                    // Main page parsing failure.
                    val failureData = workDataOf(failureKey to parseDataResult.data.toString())
                    return@withContext Result.failure(failureData)
                }
            }
        } catch (ex: Exception) {
            val msg = getExceptionMessage(ex)
            val data = workDataOf(failureKey to msg)
            Log.e("TAG", "[MeetingWorker].doWork: Exception= $msg")
            return@withContext Result.failure(data)
        } finally {
            raceDayParser?.closeStream()
        }
    }

    //<editor-fold default state="collapsed" desc="Region: Utility/Helper methods.">
    /**
     * Create the Meeting and Race objects and write to the database.
     * @param count: The listing count as returned by the parsing.
     */
    private fun generateData(count: Int): DataResult<Any> {
        var ndx = 0                      // master index var.
        var eNdx: Int                    // end of range index.
        var mtg: Map<String, String>     // raw Meeting details.
        var meeting: MeetingDBEntity
        var lRaces: MutableList<MutableMap<String, String>>  // type because of sublist().

        try {
            // Begin iterating over the listing.
            while (ndx < count) {
                mtg = listing[ndx]                          // this should be a Meeting.
                meeting = collateMeeting(mtg)               // create Meeting object.
                ndx += 1                                    // increment to 1st Race.
                eNdx = findLastRace(ndx, count)             // last Race index.
                lRaces = listing.subList(ndx, eNdx)         // create sublist of Races.

                /* TODO - MeetingType excludes all except "R" (T and G is TBA).
                *       - MeetingCode excludes those that end in "S" (mainly overseas races).
                */
                if(meeting.meetingType == "R" /* &&
                        !meeting.meetingCode.endsWith("S") */) {
                    // Update Meeting object with track & weather details from the Race detail. Each
                    // element in lRaces will have the same info, so just pick the first.
                    val id = writeMeeting(meeting, lRaces[0])
                    meeting.id = id
                    // Write the Race information.
                    writeRaces(meeting, lRaces)
                }
                //
                ndx = eNdx    // this should be the next meeting.
            }
        }  catch(ex: Exception) {
            return DataResult.Error(getExceptionMessage(ex))
        }
        return DataResult.Success("")
    }

    /**
     * Initial collate of Meeting related values.
     * @param item: A map of the Meeting info.
     * @return A MeetingDBEntity.
     */
    private fun collateMeeting(item: Map<String, String>): MeetingDBEntity {
        /* Notes:
           This method falls under the general try/catch associated with doWork() -> generateData().
           The text in the [] brackets must match the Xml text.
        */
        return MeetingDBEntity().apply {
            meetingId = item["MtgId"] ?: ""
            hiRaceNo = item["HiRaceNo"]!!
            meetingCode = item["MeetingCode"]!!
            meetingType = item["MeetingType"] ?: ""
            venueName = item["VenueName"] ?: ""
            abandoned = item["Abandoned"] ?: ""
        }
    }

    /**
     * Update Meeting object and write to database.
     * @param meeting: Previously created Meeting.
     * @param lRaceInfo: A map of selected Race info (to be added to the Meeting).
     */
    private fun writeMeeting(meeting: MeetingDBEntity, lRaceInfo: MutableMap<String, String>): Long {
        /* Note:
           This method falls under the general try/catch associated with doWork() -> generateData().
        */
        var id: Long = 0
        try {
            // Update Meeting object.
            meeting.trackDesc = lRaceInfo["TrackDesc"] ?: ""
            meeting.trackCond = lRaceInfo["TrackCond"] ?: ""
            meeting.weatherDesc = lRaceInfo["WeatherDesc"] ?: ""
            // Insert meeting object into the the database.
            id = raceMeetingDao.insertMeeting(meeting)
        } catch(ex: Exception) {
            Log.e("TAG",
                "[MeetingWorker].writeMeeting: Exception= ${getExceptionMessage(ex)}")
        }
        return id
    }

    /**
     * Write the Races to the database. Also updates the associated Meeting entry with date/time
     * values (which are on the Race record as RaceTime).
     * @param meeting: The Meeting.
     * @param lItems: A map list of Race info.
     */
    private fun writeRaces(meeting: MeetingDBEntity, lItems: MutableList<MutableMap<String, String>>) {
        /* Note:
           This method falls under the general try/catch associated with doWork() -> generateData().
        */
        val lRaceDBEntity = arrayListOf<RaceDBEntity>()
        val raceDate = getRaceDate(lItems[0]["RaceTime"])  // date is same for all.
        // Collate the list of Races.
        lItems.forEach { item ->
            try {
                val race = RaceDBEntity().apply {
                    mtgId = meeting.id
                    mtgCode = meeting.meetingCode
                    mtgVenue = meeting.venueName
                    raceNo = item["RaceNo"] ?: ""
                    raceTime = getRaceTime(item["RaceTime"])
                    raceName = item["RaceName"] ?: ""
                    distance = item["Distance"]?.let { value -> value + "m" } ?: ""
                }
                lRaceDBEntity.add(race)
            } catch(ex: Exception) {
                Log.e("TAG",
                    "[MeetingWorker].writeRaces: Exception= ${getExceptionMessage(ex)}")
            }
        }
        try {
            // Insert Race object into the the database.
            raceMeetingDao.insertRaces(lRaceDBEntity)
            // Update Meeting entry with date and time values of the 1st Race.
            raceMeetingDao.updateMeetingDateTime(raceDate, lRaceDBEntity[0].raceTime, meeting.id!!)
        } catch (ex: Exception) {
            Log.e("TAG",
                "[MeetingWorker].writeRaces: Exception= ${getExceptionMessage(ex)}")
        }
    }

    /**
     * Given a starting index value in the listing, finds the index of the last Race item before the
     * next Meeting element.
     * @param ndx: The starting index.
     * @param uBound: The upper bound to check against.
     * @return The index of the next Meeting.
     */
    private fun findLastRace(ndx: Int, uBound: Int): Int {
        var index = ndx
        var exit = 0
        while(exit != 1) {
            if (index < uBound) {
                val item = listing[index]
                if (item["MtgId"] == null) {
                    index += 1
                } else {
                    exit = 1
                }
            } else {
                exit = 1
            }
        }
        return index
    }

    /**
     * Utility method to get the Time part of the RaceTime.
     * @param dateTime: The full RaceTime value, e.g. RaceTime="2021-08-05T13:47:00"
     * @return The date part reformatted, e.g. "05-08-2021".
     */
    private fun getRaceDate(dateTime: String?): String {
        return dateTime?.let { value ->
            val list = value.split("T")[0].split("-")
            list[2] + "-" + list[1] + "-" + list[0]
        } ?: ""
    }

    /**
     * Utility method to get the Time part of the RaceTime.
     * @param dateTime: The full RaceTime value, e.g. RaceTime="2021-08-05T13:47:00"
     * @return A string in format, e.g. "13:47".
     */
    private fun getRaceTime(dateTime: String?): String {
        return dateTime?.let { value ->
            val list = value.split("T")[1].split(":")
            list[0] + ":" + list[1]
        } ?: ""
    }

    /**
     * Generate error message.
     */
    private fun getExceptionMessage(ex: Exception): String {
        return StringBuilder().apply {
            append("Message: ${ex.message}")
            appendLine()
            append("Cause: ${ex.cause}")
        }.toString()
    }
    //</editor-fold>

}
