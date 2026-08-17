package com.nebulatech.lumi.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nebulatech.lumi.calendar.components.CalendarDayType
import com.nebulatech.lumi.calendar.components.CalendarGridCell
import com.nebulatech.lumi.data.model.Cycle
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.data.model.DailyLog
import com.nebulatech.lumi.data.model.FlowIntensityType
import com.nebulatech.lumi.data.repository.CycleRepository
import com.nebulatech.lumi.data.repository.DailyLogRepository
import com.nebulatech.lumi.data.repository.RoomUserRepository
import com.nebulatech.lumi.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    private val userRepository: UserRepository,
    private val cycleRepository: CycleRepository,
    private val dailyLogRepository: DailyLogRepository
) : ViewModel() {

    private val userId = RoomUserRepository.DEFAULT_LOCAL_USER_ID
    private val selectedYearMonth = MutableStateFlow(YearMonth.now())
    private val today = LocalDate.now()

    val state: StateFlow<CalendarState> = selectedYearMonth.flatMapLatest { ym ->
        val startOfMonth = ym.atDay(1).minusDays(7)
        val endOfMonth = ym.atEndOfMonth().plusDays(7)

        combine(
            userRepository.getCurrentUser(),
            cycleRepository.getAllCycles(userId),
            dailyLogRepository.getLogsInRange(userId, startOfMonth, endOfMonth),
            cycleRepository.getAverageCycleLength(userId),
            cycleRepository.getCurrentPhase(userId, today)
        ) { user, cycles, logs, avgCycleLength, currentPhase ->
            val cycleDay = calculateCycleDay(cycles, today)
            val logsMap = logs.associateBy { it.logDate }
            val cells = buildCalendarGrid(ym, cycles, logsMap, avgCycleLength)
            val daysUntilNext = maxOf(avgCycleLength - cycleDay, 1)

            CalendarState(
                isLoading = false,
                selectedYearMonth = ym,
                gridCells = cells,
                currentPhase = currentPhase,
                cycleDay = cycleDay,
                daysUntilNextPeriod = daysUntilNext,
                cycleLength = avgCycleLength,
                phaseDescription = buildPhaseDescription(currentPhase),
                userName = user?.name ?: ""
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CalendarState()
    )

    fun onAction(action: CalendarAction) {
        when (action) {
            is CalendarAction.ChangeMonth -> selectedYearMonth.value = action.yearMonth
            is CalendarAction.SelectDay -> Unit
        }
    }

    private fun calculateCycleDay(cycles: List<Cycle>, targetDate: LocalDate): Int {
        val currentCycle = cycles.firstOrNull { it.isCurrent }
        return if (currentCycle != null) {
            val start = try { LocalDate.parse(currentCycle.startDate) } catch (_: Exception) { targetDate }
            maxOf(ChronoUnit.DAYS.between(start, targetDate).toInt() + 1, 1)
        } else {
            1
        }
    }

    private fun buildCalendarGrid(
        yearMonth: YearMonth,
        cycles: List<Cycle>,
        logsMap: Map<String, DailyLog>,
        avgCycleLength: Int
    ): List<CalendarGridCell> {
        val list = mutableListOf<CalendarGridCell>()

        val firstDay = yearMonth.atDay(1)
        val startDayOfWeek = firstDay.dayOfWeek.value // 1 = Mon, 7 = Sun
        val leadingCount = startDayOfWeek - 1

        val prevMonth = yearMonth.minusMonths(1)
        val prevMonthLength = prevMonth.lengthOfMonth()

        // Leading days from previous month
        for (i in (prevMonthLength - leadingCount + 1)..prevMonthLength) {
            list.add(CalendarGridCell(dayNumber = i, isCurrentMonth = false))
        }

        // Current month days
        val currentMonthLength = yearMonth.lengthOfMonth()
        for (d in 1..currentMonthLength) {
            val date = yearMonth.atDay(d)
            val dateStr = date.toString()
            val log = logsMap[dateStr]
            val hasDotBelow = log != null && (log.symptoms.isNotEmpty() || log.mood != null)

            val type = deriveDayType(date, log, cycles, avgCycleLength)

            list.add(
                CalendarGridCell(
                    dayNumber = d,
                    isCurrentMonth = true,
                    type = type,
                    hasDotBelow = hasDotBelow
                )
            )
        }

        // Trailing days from next month
        val totalCells = if (list.size > 35) 42 else 35
        val trailingCount = totalCells - list.size
        for (i in 1..trailingCount) {
            list.add(CalendarGridCell(dayNumber = i, isCurrentMonth = false))
        }

        return list
    }

    private fun deriveDayType(
        date: LocalDate,
        log: DailyLog?,
        cycles: List<Cycle>,
        avgCycleLength: Int
    ): CalendarDayType {
        // 1. Direct logged flow in Room has highest precedence
        if (log?.flowIntensity != null) {
            return when (log.flowIntensity) {
                FlowIntensityType.HEAVY, FlowIntensityType.MEDIUM -> CalendarDayType.PERIOD_HEAVY
                FlowIntensityType.LIGHT, FlowIntensityType.SPOTTING -> CalendarDayType.PERIOD_LIGHT
                FlowIntensityType.NONE -> CalendarDayType.NORMAL
            }
        }

        // 2. Cycle-based period and fertility calculation
        for (cycle in cycles) {
            val cycleStart = try { LocalDate.parse(cycle.startDate) } catch (_: Exception) { null } ?: continue
            val periodLen = cycle.periodLength ?: 5
            val cLength = cycle.cycleLength ?: avgCycleLength

            // Period days within the cycle
            if (!date.isBefore(cycleStart) && date.isBefore(cycleStart.plusDays(periodLen.toLong()))) {
                return if (cycle.isCurrent && !date.isBefore(today)) {
                    // Future period day on current cycle — unconfirmed prediction
                    CalendarDayType.PERIOD_PREDICTED
                } else {
                    val isFirstOrLast = date.isEqual(cycleStart) || date.isEqual(cycleStart.plusDays(periodLen.toLong() - 1))
                    if (isFirstOrLast) CalendarDayType.PERIOD_LIGHT else CalendarDayType.PERIOD_HEAVY
                }
            }

            // Predicted next period window for the current cycle (when past cycle length)
            if (cycle.isCurrent) {
                val predictedNextStart = cycleStart.plusDays(cLength.toLong())
                val predictedPeriodEnd = predictedNextStart.plusDays(periodLen.toLong())
                if (!date.isBefore(predictedNextStart) && date.isBefore(predictedPeriodEnd)) {
                    return CalendarDayType.PERIOD_PREDICTED
                }
            }

            // Ovulation & Fertile window
            val ovuDate = if (cycle.ovulationDate != null) {
                try { LocalDate.parse(cycle.ovulationDate) } catch (_: Exception) { null }
            } else {
                cycleStart.plusDays(maxOf(cLength - 14, periodLen + 1).toLong())
            }

            if (ovuDate != null) {
                if (date.isEqual(ovuDate)) {
                    return CalendarDayType.OVULATION
                }
                if (date.isEqual(ovuDate.minusDays(1)) || date.isEqual(ovuDate.minusDays(2))) {
                    return CalendarDayType.FERTILE_FILLED
                }
                if (date.isEqual(ovuDate.minusDays(3)) || date.isEqual(ovuDate.minusDays(4)) || date.isEqual(ovuDate.plusDays(1))) {
                    return CalendarDayType.FERTILE_OUTLINE
                }
            }
        }

        return CalendarDayType.NORMAL
    }

    private fun buildPhaseDescription(phase: CyclePhase): String = when (phase) {
        CyclePhase.MENSTRUATION -> "Estrogen and progesterone are low. Rest and nourish your body."
        CyclePhase.FOLLICULAR -> "Estrogen levels are rising. You might feel an increase in energy and focus today."
        CyclePhase.FERTILE_WINDOW -> "Luteinizing hormone is peaking. Conception probability is highest."
        CyclePhase.LUTEAL -> "Progesterone is high. Prioritize restorative movement and balanced nutrition."
        CyclePhase.LATE_LUTEAL -> "Hormones are declining. Hydration and magnesium can help ease PMS symptoms."
        CyclePhase.PERIOD_PREDICTED -> "Your period is predicted to start soon. Log your flow to confirm the start of your new cycle."
    }
}
