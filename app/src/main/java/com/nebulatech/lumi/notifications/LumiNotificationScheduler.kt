package com.nebulatech.lumi.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object LumiNotificationScheduler {

    private const val ID_DAILY_REFLECTION = 2001
    private const val ID_MORNING_BBT = 2002
    private const val ID_PERIOD_ALERT = 2003
    private const val ID_FERTILITY_ALERT = 2004
    private const val ID_PHASE_INSIGHT = 2005

    fun scheduleAllReminders(context: Context) {
        // 1. Daily Reflection (8:30 PM)
        scheduleDailyAlarm(
            context = context,
            hour = 20,
            minute = 30,
            requestCode = ID_DAILY_REFLECTION,
            action = LumiNotificationReceiver.ACTION_DAILY_REFLECTION,
            title = "Daily Reflection",
            body = "How are you feeling today? Take 10 seconds to log today's symptoms and mood.",
            channelId = LumiNotificationChannels.CHANNEL_DAILY_REMINDERS
        )

        // 2. Morning BBT Reading (7:00 AM)
        scheduleDailyAlarm(
            context = context,
            hour = 7,
            minute = 0,
            requestCode = ID_MORNING_BBT,
            action = LumiNotificationReceiver.ACTION_MORNING_BBT,
            title = "Good Morning",
            body = "Remember to take your waking temperature before getting up.",
            channelId = LumiNotificationChannels.CHANNEL_DAILY_REMINDERS
        )

        // 3. Period Prediction Check (9:00 AM)
        scheduleDailyAlarm(
            context = context,
            hour = 9,
            minute = 0,
            requestCode = ID_PERIOD_ALERT,
            action = LumiNotificationReceiver.ACTION_PERIOD_ALERT,
            title = "Period Prediction",
            body = "Your period is predicted in ~2 days. Stay hydrated and prioritize restorative rest.",
            channelId = LumiNotificationChannels.CHANNEL_CYCLE_PREDICTIONS
        )

        // 4. Fertile & Ovulation Alert (11:00 AM)
        scheduleDailyAlarm(
            context = context,
            hour = 11,
            minute = 0,
            requestCode = ID_FERTILITY_ALERT,
            action = LumiNotificationReceiver.ACTION_FERTILITY_ALERT,
            title = "Peak Vitality",
            body = "Peak fertility window today! Today is an optimal time for an LH ovulation test.",
            channelId = LumiNotificationChannels.CHANNEL_FERTILITY_ALERTS
        )

        // 5. Phase Insight (10:00 AM)
        scheduleDailyAlarm(
            context = context,
            hour = 10,
            minute = 0,
            requestCode = ID_PHASE_INSIGHT,
            action = LumiNotificationReceiver.ACTION_PHASE_INSIGHT,
            title = "Cycle Phase Insight",
            body = "You've entered a new phase. Check Lumi for today's hormone and energy forecast.",
            channelId = LumiNotificationChannels.CHANNEL_PHASE_INSIGHTS
        )
    }

    private fun scheduleDailyAlarm(
        context: Context,
        hour: Int,
        minute: Int,
        requestCode: Int,
        action: String,
        title: String,
        body: String,
        channelId: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, LumiNotificationReceiver::class.java).apply {
            this.action = action
            putExtra(LumiNotificationReceiver.EXTRA_TITLE, title)
            putExtra(LumiNotificationReceiver.EXTRA_BODY, body)
            putExtra(LumiNotificationReceiver.EXTRA_CHANNEL_ID, channelId)
            putExtra(LumiNotificationReceiver.EXTRA_NOTIFICATION_ID, requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    /**
     * Instantly fires a notification into the status bar and in-app Notification Center.
     * Useful for immediate testing and event-driven cycle milestones.
     */
    fun triggerNotificationNow(
        context: Context,
        title: String = "Lumi Cycle Alert",
        body: String = "Your daily insights and cycle prediction are ready in Lumi.",
        channelId: String = LumiNotificationChannels.CHANNEL_CYCLE_PREDICTIONS,
        notificationId: Int = 3001
    ) {
        val intent = Intent(context, LumiNotificationReceiver::class.java).apply {
            action = LumiNotificationReceiver.ACTION_PERIOD_ALERT
            putExtra(LumiNotificationReceiver.EXTRA_TITLE, title)
            putExtra(LumiNotificationReceiver.EXTRA_BODY, body)
            putExtra(LumiNotificationReceiver.EXTRA_CHANNEL_ID, channelId)
            putExtra(LumiNotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        context.sendBroadcast(intent)
    }
}
