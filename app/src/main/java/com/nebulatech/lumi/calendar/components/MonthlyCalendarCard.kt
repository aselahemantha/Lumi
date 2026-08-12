package com.nebulatech.lumi.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary
import java.time.YearMonth

enum class CalendarDayType {
    NORMAL,
    PERIOD_LIGHT,     // Light purple background
    PERIOD_HEAVY,     // Solid plum background
    FERTILE_OUTLINE,  // Pink outline
    FERTILE_FILLED,   // Soft pink filled
    OVULATION         // Pink circle + purple border ring + top right dot
}

data class CalendarGridCell(
    val dayNumber: Int,
    val isCurrentMonth: Boolean = true,
    val type: CalendarDayType = CalendarDayType.NORMAL,
    val hasDotBelow: Boolean = false
)

@Composable
fun MonthlyCalendarCard(
    yearMonth: YearMonth = YearMonth.of(2023, 10),
    cells: List<CalendarGridCell> = generateCalendarCells(yearMonth),
    onDayClick: (CalendarGridCell) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 14.dp)
        ) {
            // Days of Week Header (M T W T F S S)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                    Text(
                        text = day,
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8A7B84),
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7 Columns Grid
            val rows = cells.chunked(7)
            rows.forEachIndexed { _, rowCells ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    rowCells.forEach { cell ->
                        CalendarCellItem(
                            cell = cell,
                            onClick = { onDayClick(cell) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarCellItem(
    cell: CalendarGridCell,
    onClick: () -> Unit
) {
    val textColor = when {
        !cell.isCurrentMonth -> Color(0xFFD4C8CD)
        cell.type == CalendarDayType.PERIOD_HEAVY -> Color.White
        cell.type == CalendarDayType.FERTILE_FILLED -> Color(0xFF532E3E)
        cell.type == CalendarDayType.OVULATION -> Primary
        else -> Color(0xFF26181F)
    }

    Box(
        modifier = Modifier
            .size(38.dp)
            .clickable(enabled = cell.isCurrentMonth) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when (cell.type) {
            CalendarDayType.PERIOD_HEAVY -> {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${cell.dayNumber}",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
            CalendarDayType.PERIOD_LIGHT -> {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE4D5DC)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${cell.dayNumber}",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4A2B39)
                    )
                }
            }
            CalendarDayType.FERTILE_OUTLINE -> {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, Color(0xFFF7DDE6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${cell.dayNumber}",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        color = textColor
                    )
                }
            }
            CalendarDayType.FERTILE_FILLED -> {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDE8EF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${cell.dayNumber}",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                }
            }
            CalendarDayType.OVULATION -> {
                Box(
                    modifier = Modifier.size(38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFDE8EF))
                            .border(1.5.dp, Primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${cell.dayNumber}",
                            fontFamily = ManropeFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(Primary)
                    )
                }
            }
            CalendarDayType.NORMAL -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${cell.dayNumber}",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        fontWeight = if (cell.isCurrentMonth) FontWeight.Normal else FontWeight.Light,
                        color = textColor
                    )
                    if (cell.hasDotBelow) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF095AC))
                        )
                    }
                }
            }
        }
    }
}

fun generateCalendarCells(yearMonth: YearMonth): List<CalendarGridCell> {
    val list = mutableListOf<CalendarGridCell>()

    val firstDay = yearMonth.atDay(1)
    val startDayOfWeek = firstDay.dayOfWeek.value // 1 = Monday, 7 = Sunday
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
        val type = when {
            // Odd month sample cycle mapping (e.g. October 2023)
            yearMonth.monthValue % 2 == 1 && d in 3..7 -> if (d == 3 || d == 7) CalendarDayType.PERIOD_LIGHT else CalendarDayType.PERIOD_HEAVY
            yearMonth.monthValue % 2 == 1 && d in 13..14 -> CalendarDayType.FERTILE_OUTLINE
            yearMonth.monthValue % 2 == 1 && d in 15..16 -> CalendarDayType.FERTILE_FILLED
            yearMonth.monthValue % 2 == 1 && d == 17 -> CalendarDayType.OVULATION

            // Even month sample cycle mapping
            yearMonth.monthValue % 2 == 0 && d in 1..5 -> if (d == 1 || d == 5) CalendarDayType.PERIOD_LIGHT else CalendarDayType.PERIOD_HEAVY
            yearMonth.monthValue % 2 == 0 && d in 11..12 -> CalendarDayType.FERTILE_OUTLINE
            yearMonth.monthValue % 2 == 0 && d in 13..14 -> CalendarDayType.FERTILE_FILLED
            yearMonth.monthValue % 2 == 0 && d == 15 -> CalendarDayType.OVULATION

            else -> CalendarDayType.NORMAL
        }
        val hasDotBelow = (yearMonth.monthValue % 2 == 1 && d == 12) || (yearMonth.monthValue % 2 == 0 && d == 10)

        list.add(
            CalendarGridCell(
                dayNumber = d,
                isCurrentMonth = true,
                type = type,
                hasDotBelow = hasDotBelow
            )
        )
    }

    // Trailing days from next month to complete grid
    val totalCells = if (list.size > 35) 42 else 35
    val trailingCount = totalCells - list.size
    for (i in 1..trailingCount) {
        list.add(CalendarGridCell(dayNumber = i, isCurrentMonth = false))
    }

    return list
}
