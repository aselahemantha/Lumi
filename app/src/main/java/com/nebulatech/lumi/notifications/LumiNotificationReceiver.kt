package com.nebulatech.lumi.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nebulatech.lumi.MainActivity
import com.nebulatech.lumi.R
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.data.repository.CycleRepository
import com.nebulatech.lumi.data.repository.DailyLogRepository
import com.nebulatech.lumi.data.repository.NotificationRepository
import com.nebulatech.lumi.data.repository.RoomCycleRepository
import com.nebulatech.lumi.data.repository.RoomUserRepository
import com.nebulatech.lumi.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

class LumiNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_MORNING_CHECK = "com.nebulatech.lumi.ACTION_MORNING_CHECK"
        const val ACTION_PERIOD_ALERT = "com.nebulatech.lumi.ACTION_PERIOD_ALERT"
        const val ACTION_FERTILITY_ALERT = "com.nebulatech.lumi.ACTION_FERTILITY_ALERT"
        const val ACTION_PHASE_INSIGHT = "com.nebulatech.lumi.ACTION_PHASE_INSIGHT"
        const val ACTION_MORNING_BBT = "com.nebulatech.lumi.ACTION_MORNING_BBT"
        const val ACTION_DAILY_REFLECTION = "com.nebulatech.lumi.ACTION_DAILY_REFLECTION"

        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
        const val EXTRA_CHANNEL_ID = "extra_channel_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val koin = getKoin()
                val userRepo = koin.get<UserRepository>()
                val cycleRepo = koin.get<CycleRepository>()
                val notifRepo = koin.get<NotificationRepository>()
                val dailyLogRepo = koin.get<DailyLogRepository>()

                val userId = RoomUserRepository.DEFAULT_LOCAL_USER_ID
                val user = userRepo.getCurrentUser().first()
                val profile = userRepo.getUserProfileFlow(userId).first()

                // Check master notification toggle
                if (user == null || profile?.notificationsEnabled == false) {
                    return@launch
                }

                val settings = notifRepo.getSettingsFlow(userId).first().associate { it.reminderType to it.isEnabled }
                val today = LocalDate.now()
                val currentCycle = cycleRepo.getCurrentCycle(userId).first()

                when (action) {
                    ACTION_MORNING_CHECK, ACTION_PERIOD_ALERT, ACTION_PHASE_INSIGHT, ACTION_MORNING_BBT -> {
                        handleMorningChecks(
                            context = context,
                            notifRepo = notifRepo,
                            dailyLogRepo = dailyLogRepo,
                            userId = userId,
                            settings = settings,
                            profile = profile,
                            currentCycle = currentCycle,
                            today = today
                        )
                    }
                    ACTION_FERTILITY_ALERT -> {
                        handleFertilityCheck(
                            context = context,
                            notifRepo = notifRepo,
                            userId = userId,
                            settings = settings,
                            profile = profile,
                            currentCycle = currentCycle,
                            today = today
                        )
                    }
                    ACTION_DAILY_REFLECTION -> {
                        handleDailyReflection(
                            context = context,
                            notifRepo = notifRepo,
                            dailyLogRepo = dailyLogRepo,
                            userId = userId,
                            settings = settings,
                            today = today
                        )
                    }
                    else -> {
                        // Manual test notification fallback
                        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Lumi Cycle Alert"
                        val body = intent.getStringExtra(EXTRA_BODY) ?: "Your daily insights are updated."
                        val channelId = intent.getStringExtra(EXTRA_CHANNEL_ID) ?: LumiNotificationChannels.CHANNEL_CYCLE_PREDICTIONS
                        val notifId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 3001)

                        dispatchNotification(
                            context = context,
                            notifRepo = notifRepo,
                            userId = userId,
                            title = title,
                            body = body,
                            channelId = channelId,
                            notificationId = notifId,
                            category = NotificationCategory.PERIOD_PREDICTION
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun handleMorningChecks(
        context: Context,
        notifRepo: NotificationRepository,
        dailyLogRepo: DailyLogRepository,
        userId: String,
        settings: Map<String, Boolean>,
        profile: com.nebulatech.lumi.data.model.UserProfile?,
        currentCycle: com.nebulatech.lumi.data.model.Cycle?,
        today: LocalDate
    ) {
        val periodAlertsOn = settings["PERIOD_START"] ?: true
        val phaseInsightsOn = settings["PHASE_INSIGHT"] ?: false
        val bbtReminderOn = settings["BBT_REMINDER"] ?: false

        if (currentCycle != null) {
            val cycleStart = try { LocalDate.parse(currentCycle.startDate) } catch (_: Exception) { today }
            val cycleLen = currentCycle.cycleLength ?: (profile?.cycleLength ?: 28)
            val periodLen = currentCycle.periodLength ?: (profile?.periodDuration ?: 5)
            val predictedPeriodDate = cycleStart.plusDays(cycleLen.toLong())
            val daysUntil = ChronoUnit.DAYS.between(today, predictedPeriodDate).toInt()
            val cycleDay = maxOf(ChronoUnit.DAYS.between(cycleStart, today).toInt() + 1, 1)
            val phase = RoomCycleRepository.calculatePhase(cycleDay, cycleLen, periodLen)

            val todaysLog = dailyLogRepo.getLogForDate(userId, today).first()
            val hasLoggedFlowToday = todaysLog?.flowIntensity != null && todaysLog.flowIntensity != com.nebulatech.lumi.data.model.FlowIntensityType.NONE

            var sentPeriodAlert = false

            // 1. Period Prediction & Approaching Alerts
            if (periodAlertsOn && !hasLoggedFlowToday) {
                when {
                    daysUntil == 2 -> {
                        dispatchNotification(
                            context = context,
                            notifRepo = notifRepo,
                            userId = userId,
                            title = "Period Prediction",
                            body = "Your period is predicted in 2 days. Stay hydrated and prioritize restorative rest.",
                            channelId = LumiNotificationChannels.CHANNEL_CYCLE_PREDICTIONS,
                            notificationId = 1001,
                            category = NotificationCategory.PERIOD_PREDICTION
                        )
                        sentPeriodAlert = true
                    }
                    daysUntil == 1 -> {
                        dispatchNotification(
                            context = context,
                            notifRepo = notifRepo,
                            userId = userId,
                            title = "Period Prediction",
                            body = "Your period is expected tomorrow. Keep your essentials close.",
                            channelId = LumiNotificationChannels.CHANNEL_CYCLE_PREDICTIONS,
                            notificationId = 1002,
                            category = NotificationCategory.PERIOD_PREDICTION
                        )
                        sentPeriodAlert = true
                    }
                    daysUntil == 0 -> {
                        dispatchNotification(
                            context = context,
                            notifRepo = notifRepo,
                            userId = userId,
                            title = "Period Expected Today",
                            body = "Your period is predicted to start today. When bleeding begins, log your first flow to confirm.",
                            channelId = LumiNotificationChannels.CHANNEL_CYCLE_PREDICTIONS,
                            notificationId = 1003,
                            category = NotificationCategory.PERIOD_PREDICTION
                        )
                        sentPeriodAlert = true
                    }
                    daysUntil < 0 && phase == CyclePhase.PERIOD_PREDICTED -> {
                        dispatchNotification(
                            context = context,
                            notifRepo = notifRepo,
                            userId = userId,
                            title = "Period Expected",
                            body = "Your period is due. Tap to log your flow and start your new cycle.",
                            channelId = LumiNotificationChannels.CHANNEL_CYCLE_PREDICTIONS,
                            notificationId = 1004,
                            category = NotificationCategory.PERIOD_PREDICTION
                        )
                        sentPeriodAlert = true
                    }
                }
            }

            // 2. Cycle Phase & Morning Readiness Insight (if no period alert was just dispatched)
            if (phaseInsightsOn && !sentPeriodAlert) {
                val insightBody = when (phase) {
                    CyclePhase.MENSTRUATION -> "Estrogen and progesterone are at baseline. Rest, stay hydrated, and take things gently today."
                    CyclePhase.FOLLICULAR -> "Estrogen is rising steadily. Your natural energy and cognitive focus are peaking today."
                    CyclePhase.FERTILE_WINDOW -> "LH levels are elevating. You are in your fertile window today."
                    CyclePhase.LUTEAL -> "Progesterone is the dominant hormone right now. Focus on calming movement and restorative routines."
                    CyclePhase.LATE_LUTEAL -> "Progesterone is tapering down. Staying hydrated and taking magnesium can help pre-period ease."
                    CyclePhase.PERIOD_PREDICTED -> "Your period is predicted soon. Remember to log your flow when bleeding starts."
                }

                dispatchNotification(
                    context = context,
                    notifRepo = notifRepo,
                    userId = userId,
                    title = "Ready for your day? · Day $cycleDay of $cycleLen",
                    body = insightBody,
                    channelId = LumiNotificationChannels.CHANNEL_PHASE_INSIGHTS,
                    notificationId = 1005,
                    category = NotificationCategory.PHASE_INSIGHT
                )
            }
        }

        // 3. Optional Morning BBT (Default Off)
        if (bbtReminderOn) {
            dispatchNotification(
                context = context,
                notifRepo = notifRepo,
                userId = userId,
                title = "Good Morning",
                body = "Remember to take your waking temperature before getting up.",
                channelId = LumiNotificationChannels.CHANNEL_DAILY_REMINDERS,
                notificationId = 1006,
                category = NotificationCategory.MORNING_BBT
            )
        }
    }

    private suspend fun handleFertilityCheck(
        context: Context,
        notifRepo: NotificationRepository,
        userId: String,
        settings: Map<String, Boolean>,
        profile: com.nebulatech.lumi.data.model.UserProfile?,
        currentCycle: com.nebulatech.lumi.data.model.Cycle?,
        today: LocalDate
    ) {
        val fertilityAlertsOn = settings["FERTILE_WINDOW"] ?: true
        if (!fertilityAlertsOn || currentCycle == null) return

        val cycleStart = try { LocalDate.parse(currentCycle.startDate) } catch (_: Exception) { today }
        val cycleLen = currentCycle.cycleLength ?: (profile?.cycleLength ?: 28)
        val ovulationDay = cycleLen - 14
        val ovulationDate = cycleStart.plusDays(ovulationDay.toLong())

        if (today.isEqual(ovulationDate)) {
            dispatchNotification(
                context = context,
                notifRepo = notifRepo,
                userId = userId,
                title = "Peak Vitality · Peak Fertility Day",
                body = "Peak fertility window today! Today is an optimal time for an LH ovulation test or conception tracking.",
                channelId = LumiNotificationChannels.CHANNEL_FERTILITY_ALERTS,
                notificationId = 1007,
                category = NotificationCategory.PEAK_VITALITY
            )
        } else if (today.isEqual(ovulationDate.minusDays(1))) {
            dispatchNotification(
                context = context,
                notifRepo = notifRepo,
                userId = userId,
                title = "High Fertility Window",
                body = "Your fertile window is peaking. Ovulation is expected within the next 24–48 hours.",
                channelId = LumiNotificationChannels.CHANNEL_FERTILITY_ALERTS,
                notificationId = 1008,
                category = NotificationCategory.FERTILITY_INSIGHT
            )
        }
    }

    private suspend fun handleDailyReflection(
        context: Context,
        notifRepo: NotificationRepository,
        dailyLogRepo: DailyLogRepository,
        userId: String,
        settings: Map<String, Boolean>,
        today: LocalDate
    ) {
        val dailyLogOn = settings["DAILY_LOG"] ?: false
        if (!dailyLogOn) return

        val existingLog = dailyLogRepo.getLogForDate(userId, today).first()
        val hasLoggedToday = existingLog != null && (existingLog.symptoms.isNotEmpty() || existingLog.mood != null || existingLog.flowIntensity != null)

        if (!hasLoggedToday) {
            dispatchNotification(
                context = context,
                notifRepo = notifRepo,
                userId = userId,
                title = "Daily Reflection",
                body = "How are you feeling tonight? Take 10 seconds to log today's symptoms and mood.",
                channelId = LumiNotificationChannels.CHANNEL_DAILY_REMINDERS,
                notificationId = 1009,
                category = NotificationCategory.DAILY_REFLECTION
            )
        }
    }

    private suspend fun dispatchNotification(
        context: Context,
        notifRepo: NotificationRepository,
        userId: String,
        title: String,
        body: String,
        channelId: String,
        notificationId: Int,
        category: NotificationCategory
    ) {
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
        } catch (_: SecurityException) {
            // Permission not granted
        }

        // Persist to in-app notification center
        notifRepo.addNotification(
            userId = userId,
            item = LumiNotificationItem(
                id = UUID.randomUUID().toString(),
                category = category,
                title = title,
                body = body,
                timeText = "Just now"
            )
        )
    }
}
