package com.mcssoft.racedaytwo.utility

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import javax.inject.Inject

class Alarm @Inject constructor(private val context: Context) {

    private var alarmCancelled = true
    private lateinit var alarmIntent: PendingIntent
    private lateinit var alarmManager: AlarmManager

    /**
     * Set a repeating alarm.
     * @param interval: The repeat interval in minutes.
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    fun setAlarm(interval: Long) {
        // Cancel any previously set alarm.
        cancelAlarm()
        // Set the interval equivalent in mSec.
        val intervalMillis = interval * 60 * 1000
        // Set the alarm manager and intent.
        val intent = Intent(context, AlarmReceiver::class.java)
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = PendingIntent.getBroadcast(context, Constants.REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervalMillis, alarmIntent)
        // Set cancelled flag.
        alarmCancelled = false
    }

    fun cancelAlarm() {
        if(!alarmCancelled) {
            alarmManager.cancel(alarmIntent)
            alarmCancelled = true
        }
    }

}