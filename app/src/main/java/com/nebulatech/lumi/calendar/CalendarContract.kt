package com.nebulatech.lumi.calendar

import com.nebulatech.lumi.calendar.components.CalendarGridCell
import com.nebulatech.lumi.data.model.CyclePhase
import java.time.YearMonth

data class CalendarState(
    val isLoading: Boolean = true,
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val gridCells: List<CalendarGridCell> = emptyList(),
    val currentPhase: CyclePhase = CyclePhase.FOLLICULAR,
    val cycleDay: Int = 1,
    val daysUntilNextPeriod: Int = 21,
    val cycleLength: Int = 28,
    val phaseDescription: String = "",
    val userName: String = ""
)

sealed interface CalendarAction {
    data class ChangeMonth(val yearMonth: YearMonth) : CalendarAction
    data class SelectDay(val day: CalendarGridCell) : CalendarAction
}
