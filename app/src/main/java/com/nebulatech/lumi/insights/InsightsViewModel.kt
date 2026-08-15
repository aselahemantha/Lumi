package com.nebulatech.lumi.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nebulatech.lumi.data.model.Cycle
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.data.model.DailyLog
import com.nebulatech.lumi.data.repository.CycleRepository
import com.nebulatech.lumi.data.repository.DailyLogRepository
import com.nebulatech.lumi.data.repository.RoomUserRepository
import com.nebulatech.lumi.data.repository.UserRepository
import com.nebulatech.lumi.insights.components.CycleHistoryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

class InsightsViewModel(
    private val userRepository: UserRepository,
    private val cycleRepository: CycleRepository,
    private val dailyLogRepository: DailyLogRepository
) : ViewModel() {

    private val userId = RoomUserRepository.DEFAULT_LOCAL_USER_ID
    private val today = LocalDate.now()

    val state: StateFlow<InsightsState> = combine(
        userRepository.getCurrentUser(),
        cycleRepository.getAllCycles(userId),
        cycleRepository.getAverageCycleLength(userId),
        cycleRepository.getCycleDay(userId, today),
        dailyLogRepository.getLogsInRange(userId, today.minusDays(60), today)
    ) { user, cycles, avgCycleLength, cycleDay, logs ->
        val currentPhase = deriveCurrentPhase(cycles, today, avgCycleLength)
        val historyItems = buildCycleHistory(cycles, avgCycleLength)
        val symptomPoints = buildSymptomPoints(logs, avgCycleLength)
        val insightText = buildDynamicInsight(currentPhase, avgCycleLength, logs)

        InsightsState(
            isLoading = false,
            cycleHistory = historyItems,
            currentCycleDay = cycleDay,
            cycleLength = avgCycleLength,
            currentPhase = currentPhase,
            loggedSymptomPoints = symptomPoints,
            dynamicInsightText = insightText,
            hasCloudSync = user?.email?.isNotBlank() == true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InsightsState()
    )

    private fun deriveCurrentPhase(cycles: List<Cycle>, targetDate: LocalDate, avgCycleLength: Int): CyclePhase {
        val currentCycle = cycles.firstOrNull { it.isCurrent } ?: cycles.maxByOrNull { it.startDate }
        if (currentCycle == null) return CyclePhase.FOLLICULAR

        val start = try { LocalDate.parse(currentCycle.startDate) } catch (e: Exception) { targetDate }
        val day = maxOf(ChronoUnit.DAYS.between(start, targetDate).toInt() + 1, 1)
        val periodLen = currentCycle.periodLength ?: 5
        val cycleLen = currentCycle.cycleLength ?: avgCycleLength
        val ovuDay = maxOf(cycleLen - 14, periodLen + 1)

        return when {
            day <= periodLen -> CyclePhase.MENSTRUATION
            day in (ovuDay - 3)..(ovuDay + 1) -> CyclePhase.FERTILE_WINDOW
            day < ovuDay - 3 -> CyclePhase.FOLLICULAR
            day >= cycleLen - 3 -> CyclePhase.LATE_LUTEAL
            else -> CyclePhase.LUTEAL
        }
    }

    private fun buildCycleHistory(cycles: List<Cycle>, avgCycleLength: Int): List<CycleHistoryItem> {
        if (cycles.isEmpty()) {
            return listOf(
                CycleHistoryItem(
                    month = today.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    days = avgCycleLength,
                    status = "Current"
                )
            )
        }

        return cycles.sortedByDescending { it.startDate }.take(6).map { cycle ->
            val date = try { LocalDate.parse(cycle.startDate) } catch (e: Exception) { today }
            val monthStr = date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            val length = cycle.cycleLength ?: avgCycleLength
            val isRegular = abs(length - avgCycleLength) <= 2

            val status = when {
                cycle.isCurrent -> "Active"
                isRegular -> "Regular"
                else -> "Variable"
            }

            CycleHistoryItem(
                month = monthStr,
                days = length,
                status = status
            )
        }
    }

    private fun buildSymptomPoints(logs: List<DailyLog>, avgCycleLength: Int): List<SymptomTrendPoint> {
        val points = mutableListOf<SymptomTrendPoint>()
        for (log in logs) {
            val day = log.cycleDay ?: 1
            val ratio = (day.toFloat() / avgCycleLength.toFloat()).coerceIn(0.05f, 0.95f)
            val phaseName = log.cyclePhase?.name ?: "Luteal"
            for (symptom in log.symptoms) {
                points.add(
                    SymptomTrendPoint(
                        cycleDay = day,
                        dayRatio = ratio,
                        symptomName = symptom.symptomDisplayName,
                        phaseName = phaseName
                    )
                )
            }
        }
        return points
    }

    private fun buildDynamicInsight(
        phase: CyclePhase,
        avgCycleLength: Int,
        logs: List<DailyLog>
    ): String {
        val allSymptoms = logs.flatMap { it.symptoms }.map { it.symptomDisplayName }
        val topSymptom = allSymptoms.groupBy { it }.maxByOrNull { it.value.size }?.key

        return when {
            topSymptom != null -> {
                "We noticed you frequently log '$topSymptom'. In your ${phase.toDisplayString()} phase, adjusting your hydration and light activity can help bring your body back into balance."
            }
            phase == CyclePhase.MENSTRUATION -> {
                "Estrogen and progesterone are at baseline. Your body is directing energy toward renewal—prioritize iron-rich foods, restorative hydration, and gentle movement."
            }
            phase == CyclePhase.FOLLICULAR -> {
                "We noticed your high-energy days consistently align with your Follicular phase. Estrogen is rising steadily—making this a great time for creative projects or intense workouts."
            }
            phase == CyclePhase.FERTILE_WINDOW -> {
                "Luteinizing hormone (LH) is surging. Conception probability peaks over the next 24 to 48 hours with elevated natural vitality."
            }
            phase == CyclePhase.LUTEAL -> {
                "Progesterone is the dominant hormone. You may notice subtle shifts in appetite and energy—swapping high-intensity cardio for calming yoga helps stabilize cortisol."
            }
            else -> {
                "Hormones are tapering before your next cycle. Increasing magnesium and restful sleep today can help minimize PMS symptoms and fatigue."
            }
        }
    }

    private fun CyclePhase.toDisplayString(): String = when (this) {
        CyclePhase.MENSTRUATION -> "Menstrual"
        CyclePhase.FOLLICULAR -> "Follicular"
        CyclePhase.FERTILE_WINDOW -> "Fertile Window"
        CyclePhase.LUTEAL -> "Luteal"
        CyclePhase.LATE_LUTEAL -> "Late Luteal"
    }
}
