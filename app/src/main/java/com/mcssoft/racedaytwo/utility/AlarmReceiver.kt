package com.mcssoft.racedaytwo.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.mcssoft.racedaytwo.entity.cache.SummaryCacheEntity
import com.mcssoft.racedaytwo.utility.Constants.CURRENT_TIME_AFTER
import com.mcssoft.racedaytwo.utility.Constants.CURRENT_TIME_IN_WINDOW
import com.mcssoft.racedaytwo.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmViewModel: AlarmViewModel
    @Inject lateinit var dateUtils: DateUtilities
    @Inject lateinit var notifyUtils: NotifyUtilities

    private var summaryList = mutableListOf<SummaryCacheEntity>()

    override fun onReceive(context: Context?, intent: Intent?) {
        // Get the Summaries that are not elapsed (in time).
        summaryList = alarmViewModel.getFromSummariesCache()
        if(summaryList.size > 0) {
            // Process.
            summaryList.forEach { summary ->
                if(!summary.elapsed) {
                    // The (time) elapsed flag is not set.
                    val raceTime = dateUtils.timeToMillis(summary.raceTime)
                    when (dateUtils.compareToTime(raceTime)) {
                        CURRENT_TIME_AFTER -> {
                            alarmViewModel.setElapsed(summary)
                        }
                        CURRENT_TIME_IN_WINDOW -> {
                            alarmViewModel.setWithinWindow(summary)
                            // "BR 1 No 1", "<runner name> nearing race time: xx:xx"
                            notifyUtils.sendNotification(
                            summary.meetingCode + " " + summary.raceNo + " " + "No " + summary.runnerNo,
                            summary.runnerName + " nearing Race time: " + summary.raceTime)

                        // TODO - tap on notification displays Summary fragment.
                        }
                    }
                }
            }
        }

        Toast.makeText(context, "AlarmReceiver onReceive.", Toast.LENGTH_SHORT).show()

        // TODO - What is the Notification strategy ?
    }

    private fun setElapsed(summary: SummaryCacheEntity) {
        // Set elapsed flag in the cache.
        alarmViewModel.setElapsed(summary)
        // Remove from the listing.
        summaryList.removeAt(summaryList.indexOf(summary))
    }
}
/*
Notes:
------
1) xxx
2) xxx

 */