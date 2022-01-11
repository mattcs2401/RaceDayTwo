package com.mcssoft.racedaytwo.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.ui.activity.MainActivity
import com.mcssoft.racedaytwo.ui.fragment.SummaryFragment
import com.mcssoft.racedaytwo.utility.Constants.CHANNEL_ID
import com.mcssoft.racedaytwo.utility.Constants.NOTIFICATION_ID
import javax.inject.Inject

class NotifyUtilities @Inject constructor(private val context: Context)  {

    fun sendNotification(title: String, text: String) {
        val intent = Intent(context, MainActivity::class.java)

        val pIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bell_24px)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(0, notification)
    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.resources.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = context.resources.getString(R.string.channel_description)
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun cancel() {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}