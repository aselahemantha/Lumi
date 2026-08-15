package com.nebulatech.lumi.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object LumiNotificationChannels {

    const val CHANNEL_DAILY_REMINDERS = "lumi_daily_reminders"
    const val CHANNEL_CYCLE_PREDICTIONS = "lumi_cycle_predictions"
    const val CHANNEL_FERTILITY_ALERTS = "lumi_fertility_alerts"
    const val CHANNEL_PHASE_INSIGHTS = "lumi_phase_insights"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val dailyRemindersChannel = NotificationChannel(
                CHANNEL_DAILY_REMINDERS,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Evening reflections and morning waking temperature check-ins"
                enableVibration(true)
            }

            val cyclePredictionsChannel = NotificationChannel(
                CHANNEL_CYCLE_PREDICTIONS,
                "Period & Cycle Predictions",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Advance alerts for upcoming periods and cycle milestones"
                enableVibration(true)
            }

            val fertilityAlertsChannel = NotificationChannel(
                CHANNEL_FERTILITY_ALERTS,
                "Fertility & Ovulation Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Fertile window opening and peak LH ovulation alerts"
                enableVibration(true)
            }

            val phaseInsightsChannel = NotificationChannel(
                CHANNEL_PHASE_INSIGHTS,
                "Cycle Phase Insights",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Personalized hormone and wellness insights when entering new cycle phases"
            }

            notificationManager.createNotificationChannels(
                listOf(
                    dailyRemindersChannel,
                    cyclePredictionsChannel,
                    fertilityAlertsChannel,
                    phaseInsightsChannel
                )
            )
        }
    }
}
