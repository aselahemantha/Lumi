package com.nebulatech.lumi.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nebulatech.lumi.MainActivity
import com.nebulatech.lumi.R

class LumiNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DAILY_REFLECTION = "com.nebulatech.lumi.ACTION_DAILY_REFLECTION"
        const val ACTION_MORNING_BBT = "com.nebulatech.lumi.ACTION_MORNING_BBT"
        const val ACTION_PERIOD_ALERT = "com.nebulatech.lumi.ACTION_PERIOD_ALERT"
        const val ACTION_FERTILITY_ALERT = "com.nebulatech.lumi.ACTION_FERTILITY_ALERT"
        const val ACTION_PHASE_INSIGHT = "com.nebulatech.lumi.ACTION_PHASE_INSIGHT"

        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
        const val EXTRA_CHANNEL_ID = "extra_channel_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Lumi"
        val body = intent.getStringExtra(EXTRA_BODY) ?: "Time to check your cycle."
        val channelId = intent.getStringExtra(EXTRA_CHANNEL_ID)
            ?: LumiNotificationChannels.CHANNEL_DAILY_REMINDERS
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 1001)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(
                if (channelId == LumiNotificationChannels.CHANNEL_CYCLE_PREDICTIONS ||
                    channelId == LumiNotificationChannels.CHANNEL_FERTILITY_ALERTS
                ) {
                    NotificationCompat.PRIORITY_HIGH
                } else {
                    NotificationCompat.PRIORITY_DEFAULT
                }
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(0xFF8E5572.toInt())
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Permission not granted on Android 13+
        }
    }
}
