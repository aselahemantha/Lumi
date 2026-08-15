package com.nebulatech.lumi.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.data.model.PrimaryGoal
import com.nebulatech.lumi.data.repository.CycleRepository
import com.nebulatech.lumi.data.repository.DailyLogRepository
import com.nebulatech.lumi.data.repository.RoomUserRepository
import com.nebulatech.lumi.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class NotificationCenterViewModel(
    private val userRepository: UserRepository,
    private val cycleRepository: CycleRepository,
    private val dailyLogRepository: DailyLogRepository
) : ViewModel() {

    private val userId = RoomUserRepository.DEFAULT_LOCAL_USER_ID

    val state: StateFlow<NotificationCenterState> = combine(
        userRepository.getUserProfileFlow(userId),
        cycleRepository.getCurrentCycle(userId),
        cycleRepository.getAverageCycleLength(userId),
        cycleRepository.getCurrentPhase(userId),
        dailyLogRepository.getLogForDate(userId, LocalDate.now())
    ) { profile, currentCycle, avgLength, currentPhase, todayLog ->
        val cycleLength = if (avgLength > 0) avgLength else (profile?.cycleLength ?: 28)
        val currentCycleDay = if (currentCycle != null) {
            try {
                val start = LocalDate.parse(currentCycle.startDate)
                (ChronoUnit.DAYS.between(start, LocalDate.now()).toInt() + 1).coerceAtLeast(1)
            } catch (e: Exception) { 1 }
        } else { 1 }

        val daysUntilPeriod = (cycleLength - currentCycleDay).coerceAtLeast(0)
        val estimatedOvulationDay = (cycleLength - 14).coerceAtLeast(10)
        val hasLoggedToday = todayLog != null

        val list = mutableListOf<LumiNotificationItem>()

        // 1. Daily Reflection (8:30 PM)
        list.add(
            LumiNotificationItem(
                id = "notif_reflection",
                category = NotificationCategory.DAILY_REFLECTION,
                title = "Daily Reflection",
                body = if (hasLoggedToday) {
                    "Great job logging today! Check your updated insights and hormone forecasts."
                } else {
                    "How are you feeling today? Take 10 seconds to log today's symptoms and mood."
                },
                timeText = "8:30 PM"
            )
        )

        // 2. Morning BBT Reminder (7:00 AM)
        if (profile?.primaryGoal == PrimaryGoal.OPTIMIZE_FERTILITY ||
            profile?.primaryGoal == PrimaryGoal.AVOID_PREGNANCY ||
            profile?.primaryGoal == PrimaryGoal.TRACK_CYCLE
        ) {
            list.add(
                LumiNotificationItem(
                    id = "notif_morning_bbt",
                    category = NotificationCategory.MORNING_BBT,
                    title = "Good Morning",
                    body = "Remember to take your waking temperature before getting up.",
                    timeText = "7:00 AM"
                )
            )
        }

        // 3. Period Prediction (9:00 AM) - Dynamic with days remaining
        val periodBody = when {
            daysUntilPeriod <= 0 -> "Your period is expected today. Confirm flow in Lumi to maintain high precision."
            daysUntilPeriod == 1 -> "Your period is predicted tomorrow. Stay hydrated and prioritize restorative rest."
            daysUntilPeriod <= 3 -> "Your period is predicted in ~$daysUntilPeriod days. Stay hydrated and prioritize restorative rest."
            else -> "Your next period is estimated in $daysUntilPeriod days (Day $currentCycleDay of $cycleLength)."
        }
        list.add(
            LumiNotificationItem(
                id = "notif_period_pred",
                category = NotificationCategory.PERIOD_PREDICTION,
                title = "Period Prediction",
                body = periodBody,
                timeText = "9:00 AM",
                badgeIcon = Icons.Outlined.WaterDrop,
                badgeBgColor = Color(0xFFFDE8EF),
                badgeIconColor = Color(0xFF8E5572)
            )
        )

        // 4. Fertility Insight (9:00 AM)
        val daysUntilFertile = (estimatedOvulationDay - 5 - currentCycleDay)
        val fertilityBody = when {
            currentPhase == CyclePhase.FERTILE_WINDOW -> "You are currently in your fertile window. Chances of conception are elevated."
            daysUntilFertile in 1..2 -> "Your fertile window begins in $daysUntilFertile days. Chances of conception are rising."
            else -> "Your fertile window begins around Day ${estimatedOvulationDay - 5}. Keep tracking to identify your peak."
        }
        list.add(
            LumiNotificationItem(
                id = "notif_fertility",
                category = NotificationCategory.FERTILITY_INSIGHT,
                title = "Fertility Insight",
                body = fertilityBody,
                timeText = "9:00 AM",
                badgeIcon = Icons.Outlined.Spa,
                badgeBgColor = Color(0xFFFDE8F4),
                badgeIconColor = Color(0xFFB54876)
            )
        )

        // 5. Peak Vitality (11:00 AM)
        val vitalityBody = if (currentPhase == CyclePhase.FERTILE_WINDOW || currentCycleDay == estimatedOvulationDay) {
            "Peak fertility window today (Day $currentCycleDay)! Today is an optimal time for an LH ovulation test."
        } else {
            "High energy anticipated during your active ${currentPhase.name.lowercase().replace('_', ' ')} phase. Great window for workouts and focused tasks."
        }
        list.add(
            LumiNotificationItem(
                id = "notif_vitality",
                category = NotificationCategory.PEAK_VITALITY,
                title = "Peak Vitality",
                body = vitalityBody,
                timeText = "11:00 AM",
                badgeIcon = Icons.Outlined.AutoAwesome,
                badgeBgColor = Color(0xFFFDF0E4),
                badgeIconColor = Color(0xFFD97706)
            )
        )

        // 6. Phase Wellness Insight (10:00 AM) - Dynamic per active phase
        val (phaseTitle, phaseBody) = when (currentPhase) {
            CyclePhase.MENSTRUATION -> "Menstrual Phase" to "You're in your Menstrual phase. Support your body with warm herbal teas, iron-rich meals, and gentle movement."
            CyclePhase.FOLLICULAR -> "Follicular Phase" to "You've entered the Follicular phase. Estrogen is rising—experience a natural surge in energy, creativity, and stamina."
            CyclePhase.FERTILE_WINDOW -> "Ovulatory Window" to "Estrogen and LH are surging. Enjoy peak focus, glowing skin, and elevated communication power."
            CyclePhase.LUTEAL -> "Luteal Phase" to "You've entered the Luteal phase. Consider swapping high-intensity workouts for yoga to balance cortisol."
            CyclePhase.LATE_LUTEAL -> "Late Luteal Phase" to "Progesterone is tapering down. Extra magnesium, complex carbs, and 8+ hours of sleep can ease PMS symptoms."
        }
        list.add(
            LumiNotificationItem(
                id = "notif_phase_wellness",
                category = NotificationCategory.PHASE_INSIGHT,
                title = phaseTitle,
                body = phaseBody,
                timeText = "10:00 AM",
                badgeIcon = Icons.Outlined.Grain,
                badgeBgColor = Color(0xFFEFE8EB),
                badgeIconColor = Color(0xFF5B3950)
            )
        )

        NotificationCenterState(
            isLoading = false,
            notifications = list
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotificationCenterState(isLoading = true)
    )
}
