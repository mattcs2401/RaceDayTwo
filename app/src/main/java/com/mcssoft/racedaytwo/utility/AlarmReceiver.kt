package com.mcssoft.racedaytwo.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.mcssoft.racedaytwo.entity.cache.SummaryCacheEntity
import com.mcssoft.racedaytwo.utility.Constants.CURRENT_TIME_BEFORE
import com.mcssoft.racedaytwo.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmViewModel: AlarmViewModel
    @Inject lateinit var dateUtils: DateUtilities

    private var summaryList = mutableListOf<SummaryCacheEntity>()

    override fun onReceive(context: Context?, intent: Intent?) {
        // Get the Summaries that are not elapsed (in time).
        summaryList = alarmViewModel.getFromSummariesCache(false) as MutableList<SummaryCacheEntity>
        if(summaryList.size > 0) {
            // Process.
            summaryList.forEach { summary ->
                val raceTime = dateUtils.timeToMillis(summary.raceTime)
                when(dateUtils.compareToTime(raceTime)) {
                    CURRENT_TIME_BEFORE -> {}
                }
            }
        }

//        Toast.makeText(context, "AlarmReceiver onReceive.", Toast.LENGTH_SHORT).show()

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