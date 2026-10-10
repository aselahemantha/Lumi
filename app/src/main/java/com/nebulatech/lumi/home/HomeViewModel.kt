package com.nebulatech.lumi.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nebulatech.lumi.data.model.BbtReading
import com.nebulatech.lumi.data.model.Cycle
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.data.model.DailyLog
import com.nebulatech.lumi.data.model.LhTest
import com.nebulatech.lumi.data.repository.BbtRepository
import com.nebulatech.lumi.data.repository.CycleRepository
import com.nebulatech.lumi.data.repository.DailyLogRepository
import com.nebulatech.lumi.data.repository.LhTestRepository
import com.nebulatech.lumi.data.repository.RoomUserRepository
import com.nebulatech.lumi.data.repository.UserRepository
import com.nebulatech.lumi.home.components.BbtChartPoint
import com.nebulatech.lumi.home.components.LogEntryItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private data class TodayLogsData(
    val dailyLog: DailyLog?,
    val bbtReading: BbtReading?,
    val lhTest: LhTest?,
    val recentBbtReadings: List<BbtReading>
)

class HomeViewModel(
    userRepository: UserRepository,
    private val cycleRepository: CycleRepository,
    dailyLogRepository: DailyLogRepository,
    bbtRepository: BbtRepository,
    lhTestRepository: LhTestRepository
) : ViewModel() {

    private val userId = RoomUserRepository.DEFAULT_LOCAL_USER_ID
    private val today = LocalDate.now()

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    val state: StateFlow<HomeState> = combine(
        combine(
            userRepository.getCurrentUser(),
            cycleRepository.getCurrentCycle(userId),
            cycleRepository.getCycleDay(userId, today)
        ) { user, cycle, cycleDay -> Triple(user, cycle, cycleDay) },
        combine(
            cycleRepository.getAverageCycleLength(userId),
            cycleRepository.getAveragePeriodLength(userId),
            cycleRepository.getCurrentPhase(userId, today)
        ) { avgCycle, avgPeriod, phase -> Triple(avgCycle, avgPeriod, phase) },
        combine(
            dailyLogRepository.getLogForDate(userId, today),
            bbtRepository.getBbtForDate(userId, today),
            lhTestRepository.getLhTestForDate(userId, today),
            bbtRepository.getBbtInRange(userId, today.minusDays(35), today)
        ) { dailyLog, bbtReading, lhTest, recentBbtReadings ->
            TodayLogsData(dailyLog, bbtReading, lhTest, recentBbtReadings)
        }
    ) { (user, cycle, cycleDay), (avgCycleLength, avgPeriodLength, phase), logsData ->
        if (user == null) {
            viewModelScope.launch { _events.send(HomeEvent.NavigateToOnboarding) }
            return@combine HomeState(isLoading = false)
        }

        val progressRatio = (cycleDay.toFloat() / avgCycleLength.toFloat()).coerceIn(0f, 1f)
        val overdueDays = if (cycleDay > avgCycleLength) cycleDay - avgCycleLength else 0
        val subLabel = buildSubLabel(phase, cycleDay, avgCycleLength, avgPeriodLength, overdueDays)
        val (insightTitle, insightText) = buildInsight(phase, overdueDays, logsData.bbtReading, logsData.lhTest)
        val next7Days = buildNext7Days(today, cycle, avgCycleLength, avgPeriodLength)
        val todaysLogs = buildTodaysLogs(logsData.dailyLog, logsData.bbtReading, logsData.lhTest)
        val todayBbtTemp = logsData.bbtReading?.let { "${it.temperature}°${it.temperatureUnit}" }
        val bbtChartPoints = buildBbtChartPoints(logsData.recentBbtReadings, cycle, today)

        HomeState(
            isLoading = false,
            layoutType = phase.toLayoutType(),
            cycleDay = cycleDay,
            cycleLength = avgCycleLength,
            periodLength = avgPeriodLength,
            progressRatio = progressRatio,
            subLabelText = subLabel,
            userName = user.name,
            hasCycle = cycle != null,
            currentPhase = phase,
            insightTitle = insightTitle,
            insightText = insightText,
            isPeriodPredicted = phase == CyclePhase.PERIOD_PREDICTED,
            isPeriodMissed = phase == CyclePhase.PERIOD_MISSED,
            overdueDays = overdueDays,
            next7Days = next7Days,
            todaysLogs = todaysLogs,
            todayBbtTemperature = todayBbtTemp,
            bbtChartPoints = bbtChartPoints
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState()
    )

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.StartNewPeriod -> {
                viewModelScope.launch {
                    cycleRepository.startNewCycle(userId, today)
                }
            }
            HomeAction.MarkPeriodAsMissed -> {
                viewModelScope.launch {
                    cycleRepository.markCurrentCycleAsMissed(userId)
                }
            }
            // ConfirmPeriodStart is handled at the UI level via LoggingViewModel;
            // the flow sheet is shown and on save, LoggingViewModel auto-starts the new cycle.
            HomeAction.ConfirmPeriodStart -> Unit
        }
    }

    private fun CyclePhase.toLayoutType(): HomeLayoutType = when (this) {
        CyclePhase.MENSTRUATION -> HomeLayoutType.CYCLE_RING
        CyclePhase.FOLLICULAR -> HomeLayoutType.CYCLE_RING
        CyclePhase.FERTILE_WINDOW -> HomeLayoutType.FERTILITY_DASHBOARD
        CyclePhase.LUTEAL -> HomeLayoutType.CYCLE_RING
        CyclePhase.LATE_LUTEAL -> HomeLayoutType.SYMPTOM_GRID
        CyclePhase.PERIOD_PREDICTED -> HomeLayoutType.CYCLE_RING
        CyclePhase.PERIOD_MISSED -> HomeLayoutType.CYCLE_RING
    }

    private fun buildSubLabel(
        phase: CyclePhase,
        cycleDay: Int,
        cycleLength: Int,
        periodLength: Int,
        overdueDays: Int
    ): String {
        return when (phase) {
            CyclePhase.MENSTRUATION -> "Day $cycleDay of your period"
            CyclePhase.FOLLICULAR -> {
                val ovulationDay = cycleLength - 14
                val fertileStart = maxOf(ovulationDay - 3, periodLength + 1)
                val daysUntil = maxOf(fertileStart - cycleDay, 1)
                "Fertile window in ~$daysUntil days"
            }
            CyclePhase.FERTILE_WINDOW -> "Peak fertility window"
            CyclePhase.LUTEAL, CyclePhase.LATE_LUTEAL -> {
                val daysUntil = maxOf(cycleLength - cycleDay, 1)
                "Period in ~$daysUntil days"
            }
            CyclePhase.PERIOD_PREDICTED -> "Period expected · Log flow to confirm"
            CyclePhase.PERIOD_MISSED -> "Period is $overdueDays ${if (overdueDays == 1) "day" else "days"} late · Tap to update"
        }
    }

    private fun buildInsight(
        phase: CyclePhase,
        overdueDays: Int,
        bbtReading: BbtReading? = null,
        lhTest: LhTest? = null
    ): Pair<String, String> {
        return when (phase) {
            CyclePhase.MENSTRUATION -> {
                "Menstrual Phase Insight" to "Estrogen and progesterone are at baseline. Prioritize iron-rich foods, restorative hydration, and gentle movement today."
            }
            CyclePhase.FOLLICULAR -> {
                "Follicular Phase Insight" to "Estrogen is rising steadily. Your natural energy and cognitive focus are peaking—ideal for high-intensity training and creative projects."
            }
            CyclePhase.FERTILE_WINDOW -> {
                val extras = buildList {
                    if (bbtReading != null) add("Today's BBT reading: ${bbtReading.temperature}°${bbtReading.temperatureUnit}.")
                    if (lhTest != null) add("LH test recorded: ${lhTest.intensity.name.lowercase().replaceFirstChar { it.uppercase() }} surge.")
                }
                val details = if (extras.isNotEmpty()) " " + extras.joinToString(" ") else ""
                "Peak Fertility Window" to "Likelihood of ovulation is peaking over the next 24–48 hours.$details Consider logging BBT or LH test for optimal tracking."
            }
            CyclePhase.LUTEAL -> {
                "Luteal Phase Insight" to "Progesterone is the dominant hormone right now. Consider swapping high-intensity cardio for calming yoga or walking to balance cortisol."
            }
            CyclePhase.LATE_LUTEAL -> {
                "Pre-Menstrual Insight" to "Progesterone is tapering down before your period. Staying well-hydrated and increasing magnesium intake can help prevent PMS headaches."
            }
            CyclePhase.PERIOD_PREDICTED -> {
                "Period Expected" to "Your period is predicted to start soon. When your bleeding begins, log your flow below to start your new cycle."
            }
            CyclePhase.PERIOD_MISSED -> {
                "Missed Period Guidance" to "Your period is $overdueDays ${if (overdueDays == 1) "day" else "days"} past expected. Missed or delayed cycles can happen from stress, travel, hormonal shifts, or pregnancy. Tap below to log flow or mark this cycle as missed."
            }
        }
    }

    private fun buildTodaysLogs(
        dailyLog: DailyLog?,
        bbtReading: BbtReading?,
        lhTest: LhTest?
    ): List<LogEntryItem> {
        val items = mutableListOf<LogEntryItem>()

        if (bbtReading != null) {
            items.add(
                LogEntryItem(
                    icon = Icons.Outlined.Thermostat,
                    title = "${bbtReading.temperature}°${bbtReading.temperatureUnit}",
                    category = "Basal Body Temp"
                )
            )
        }

        if (lhTest != null) {
            val intensityName = lhTest.intensity.name.lowercase().replaceFirstChar { it.uppercase() }
            items.add(
                LogEntryItem(
                    icon = Icons.Outlined.Biotech,
                    title = "$intensityName Surge",
                    category = "LH Test"
                )
            )
        }

        if (dailyLog?.flowIntensity != null) {
            val flowName = dailyLog.flowIntensity.name.lowercase().replaceFirstChar { it.uppercase() }
            items.add(
                LogEntryItem(
                    icon = Icons.Outlined.WaterDrop,
                    title = flowName,
                    category = "Flow"
                )
            )
        }

        if (dailyLog?.mood != null) {
            val moodName = dailyLog.mood.name.lowercase().replaceFirstChar { it.uppercase() }
            items.add(
                LogEntryItem(
                    icon = Icons.Outlined.SentimentSatisfied,
                    title = moodName,
                    category = "Mood"
                )
            )
        }

        dailyLog?.symptoms?.forEach { symptom ->
            items.add(
                LogEntryItem(
                    icon = Icons.Outlined.Healing,
                    title = symptom.symptomDisplayName,
                    category = "Symptom"
                )
            )
        }

        return items
    }

    private fun buildNext7Days(
        today: LocalDate,
        cycle: com.nebulatech.lumi.data.model.Cycle?,
        avgCycleLength: Int,
        avgPeriodLength: Int
    ): List<com.nebulatech.lumi.home.components.DayItem> {
        val cycleStart = cycle?.startDate?.let {
            try { LocalDate.parse(it) } catch (_: Exception) { null }
        }
        val cLength = cycle?.cycleLength ?: avgCycleLength
        val pLength = cycle?.periodLength ?: avgPeriodLength
        val predictedNextStart = cycleStart?.plusDays(cLength.toLong())

        return (0..6).map { offset ->
            val target = today.plusDays(offset.toLong())
            val dayName = target.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
            val isToday = (offset == 0)

            val isPredictedPeriod = if (predictedNextStart != null) {
                val predictedPeriodEnd = predictedNextStart.plusDays(pLength.toLong())
                !target.isBefore(predictedNextStart) && target.isBefore(predictedPeriodEnd)
            } else false

            com.nebulatech.lumi.home.components.DayItem(
                dayName = dayName,
                dateNumber = target.dayOfMonth,
                isToday = isToday,
                hasPeriodDot = isPredictedPeriod
            )
        }
    }

    private fun buildBbtChartPoints(
        readings: List<BbtReading>,
        currentCycle: Cycle?,
        today: LocalDate
    ): List<BbtChartPoint> {
        val cycleStart = currentCycle?.startDate?.let {
            try { LocalDate.parse(it) } catch (_: Exception) { null }
        }
        return readings
            .filter { reading ->
                if (currentCycle?.id != null && reading.cycleId == currentCycle.id) true
                else if (cycleStart != null) {
                    val rDate = try { LocalDate.parse(reading.readingDate) } catch (_: Exception) { null }
                    rDate != null && !rDate.isBefore(cycleStart)
                } else true
            }
            .mapNotNull { reading ->
                val rDate = try { LocalDate.parse(reading.readingDate) } catch (_: Exception) { null }
                val cDay = reading.cycleDay ?: if (cycleStart != null && rDate != null) {
                    ChronoUnit.DAYS.between(cycleStart, rDate).toInt() + 1
                } else null

                if (cDay != null && cDay > 0) {
                    BbtChartPoint(
                        cycleDay = cDay,
                        temperature = reading.temperature.toFloat(),
                        unit = reading.temperatureUnit,
                        dateString = reading.readingDate,
                        isToday = reading.readingDate == today.toString()
                    )
                } else null
            }
            .sortedBy { it.cycleDay }
    }
}
