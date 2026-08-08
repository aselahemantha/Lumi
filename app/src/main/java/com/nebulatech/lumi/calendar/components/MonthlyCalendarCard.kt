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
    cells: List<CalendarGridCell> = generateSampleOctoberCells(),
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

            // 7 Columns Grid (5 rows of 7 days)
            val rows = cells.chunked(7)
            rows.forEachIndexed { rowIndex, rowCells ->
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
                    // Filled background
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

                    // Top-right ovulation dot indicator
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

private fun generateSampleOctoberCells(): List<CalendarGridCell> {
    val list = mutableListOf<CalendarGridCell>()

    // Row 1: Prev month 25..30, Oct 1
    listOf(25, 26, 27, 28, 29, 30).forEach { list.add(CalendarGridCell(it, isCurrentMonth = false)) }
    list.add(CalendarGridCell(1))

    // Row 2: Oct 2..8 (Period on 3,4,5,6,7)
    list.add(CalendarGridCell(2))
    list.add(CalendarGridCell(3, type = CalendarDayType.PERIOD_LIGHT))
    list.add(CalendarGridCell(4, type = CalendarDayType.PERIOD_HEAVY))
    list.add(CalendarGridCell(5, type = CalendarDayType.PERIOD_HEAVY))
    list.add(CalendarGridCell(6, type = CalendarDayType.PERIOD_HEAVY))
    list.add(CalendarGridCell(7, type = CalendarDayType.PERIOD_LIGHT))
    list.add(CalendarGridCell(8))

    // Row 3: Oct 9..15 (Fertile 13, 14, 15)
    list.add(CalendarGridCell(9))
    list.add(CalendarGridCell(10))
    list.add(CalendarGridCell(11))
    list.add(CalendarGridCell(12, hasDotBelow = true))
    list.add(CalendarGridCell(13, type = CalendarDayType.FERTILE_OUTLINE))
    list.add(CalendarGridCell(14, type = CalendarDayType.FERTILE_OUTLINE))
    list.add(CalendarGridCell(15, type = CalendarDayType.FERTILE_FILLED))

    // Row 4: Oct 16..22 (Fertile 16, Ovulation 17)
    list.add(CalendarGridCell(16, type = CalendarDayType.FERTILE_FILLED))
    list.add(CalendarGridCell(17, type = CalendarDayType.OVULATION))
    (18..22).forEach { list.add(CalendarGridCell(it)) }

    // Row 5: Oct 23..29
    (23..29).forEach { list.add(CalendarGridCell(it)) }

    // Row 6: Oct 30..31, Nov 1..5
    list.add(CalendarGridCell(30))
    list.add(CalendarGridCell(31))
    (1..5).forEach { list.add(CalendarGridCell(it, isCurrentMonth = false)) }

    return list
}
