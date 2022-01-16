package com.mcssoft.racedaytwo.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.ui.activity.SummaryActivity
import com.mcssoft.racedaytwo.utility.Constants.CHANNEL_ID
import javax.inject.Inject

class NotifyUtilities @Inject constructor(private val context: Context)  {

    fun sendNotification(title: String, text: String) {

//        val pIntent = NavDeepLinkBuilder(context)
////            .setComponentName(MainActivity::class.java)    // uses what's default in manifest.
//            .setGraph(R.navigation.navigation)
//            .setDestination(R.id.id_summary_fragment)
//            .setArguments(bundleOf("key" to "fromNotifyTap"))
//            .createPendingIntent()

        // Create an Intent for the activity you want to start
        val intent = Intent(context, SummaryActivity::class.java).apply {
            putExtras(bundleOf("key" to "fromNotifyTap"))
            // TBA - These required ?
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        // Create the TaskStackBuilder
        val pIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }


        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bell_24px)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pIntent)
//            .setVibrate(new long[]{250, 250, 250, 250})
//            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
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