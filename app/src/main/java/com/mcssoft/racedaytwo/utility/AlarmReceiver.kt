package com.mcssoft.racedaytwo.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.mcssoft.racedaytwo.utility.Constants.CURRENT_TIME_AFTER
import com.mcssoft.racedaytwo.utility.Constants.CURRENT_TIME_IN_WINDOW
import com.mcssoft.racedaytwo.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    // TODO - tap on notification displays Summary fragment.

    @Inject lateinit var alarmViewModel: AlarmViewModel
    @Inject lateinit var dateUtils: DateUtilities
    @Inject lateinit var notifyUtils: NotifyUtilities

    override fun onReceive(context: Context?, intent: Intent?) {
        // Get the Summaries that are not elapsed (in time).
        CoroutineScope(Dispatchers.IO).launch {
            alarmViewModel.getFromSummariesCache().collect { lSummary ->
                lSummary.forEach { summary ->
                    if(!summary.elapsed) {
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
                            }
                        }
                    }
                }
            }
        }
        Toast.makeText(context, "AlarmReceiver onReceive.", Toast.LENGTH_SHORT).show()
    }

}
