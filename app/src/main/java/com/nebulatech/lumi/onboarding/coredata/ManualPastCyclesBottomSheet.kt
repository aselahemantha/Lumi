package com.nebulatech.lumi.onboarding.coredata

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.onboarding.ManualPastCycle
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualPastCyclesBottomSheet(
    firstDayOfLastPeriod: LocalDate,
    defaultCycleLength: Int = 28,
    defaultPeriodDuration: Int = 5,
    initialPastCycles: List<ManualPastCycle>? = null,
    onDismissRequest: () -> Unit,
    onSave: (List<ManualPastCycle>?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    var cyclesList by remember {
        mutableStateOf(
            if (!initialPastCycles.isNullOrEmpty()) {
                initialPastCycles
            } else {
                val c1Start = firstDayOfLastPeriod.minusDays(defaultCycleLength.toLong())
                val c2Start = c1Start.minusDays(defaultCycleLength.toLong())
                val c3Start = c2Start.minusDays(defaultCycleLength.toLong())
                listOf(
                    ManualPastCycle(c1Start, defaultCycleLength, defaultPeriodDuration),
                    ManualPastCycle(c2Start, defaultCycleLength, defaultPeriodDuration),
                    ManualPastCycle(c3Start, defaultCycleLength, defaultPeriodDuration)
                )
            }
        )
    }

    val currentAvgCycleLength = if (cyclesList.isNotEmpty()) {
        round(cyclesList.map { it.cycleLength }.average()).toInt().coerceIn(20, 45)
    } else defaultCycleLength

    val currentAvgPeriodDuration = if (cyclesList.isNotEmpty()) {
        round(cyclesList.map { it.periodDuration }.average()).toInt().coerceIn(2, 10)
    } else defaultPeriodDuration

    var activeDatePickerIndex by remember { mutableStateOf<Int?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFFFBF9F7),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Manual Past Cycles",
                fontFamily = LiterataFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Enter the start dates of your previous cycles to calibrate your baseline. Tap '+' to add more past cycles for higher prediction accuracy.",
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Color(0xFF6E5E67)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Dynamic Past Cycle Cards
            cyclesList.forEachIndexed { index, cycle ->
                val title = when (index) {
                    0 -> "Cycle 1 (Previous Cycle)"
                    1 -> "Cycle 2 (2 Cycles Ago)"
                    2 -> "Cycle 3 (3 Cycles Ago)"
                    else -> "Cycle ${index + 1} (${index + 1} Cycles Ago)"
                }

                PastCycleInputCard(
                    title = title,
                    startDate = cycle.startDate,
                    cycleLength = cycle.cycleLength,
                    periodDuration = cycle.periodDuration,
                    dateFormatter = dateFormatter,
                    onDateClick = { activeDatePickerIndex = index },
                    onLengthChange = { newLen ->
                        val updated = cyclesList.toMutableList()
                        val precedingDate = if (index == 0) firstDayOfLastPeriod else updated[index - 1].startDate
                        val newStartDate = precedingDate.minusDays(newLen.toLong())
                        updated[index] = updated[index].copy(
                            cycleLength = newLen,
                            startDate = newStartDate
                        )
                        // Shift older cycles accordingly
                        for (j in (index + 1) until updated.size) {
                            updated[j] = updated[j].copy(
                                startDate = updated[j - 1].startDate.minusDays(updated[j].cycleLength.toLong())
                            )
                        }
                        cyclesList = updated
                    },
                    onDurationChange = { newDur ->
                        val updated = cyclesList.toMutableList()
                        updated[index] = updated[index].copy(periodDuration = newDur)
                        cyclesList = updated
                    },
                    onRemoveClick = if (cyclesList.size > 1) {
                        {
                            val updated = cyclesList.toMutableList()
                            updated.removeAt(index)
                            cyclesList = updated
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // "+" Button to Add Another Past Cycle
            if (cyclesList.size < 12) {
                OutlinedButton(
                    onClick = {
                        val lastDate = cyclesList.lastOrNull()?.startDate ?: firstDayOfLastPeriod
                        val newStartDate = lastDate.minusDays(defaultCycleLength.toLong())
                        cyclesList = cyclesList + ManualPastCycle(
                            startDate = newStartDate,
                            cycleLength = defaultCycleLength,
                            periodDuration = defaultPeriodDuration
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, Primary.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Another Past Cycle",
                            fontFamily = ManropeFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Calibration summary badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF3E7ED))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Average (${cyclesList.size} ${if (cyclesList.size == 1) "cycle" else "cycles"})",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6E5E67)
                )
                Text(
                    text = "$currentAvgCycleLength days cycle • $currentAvgPeriodDuration days flow",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Button(
                onClick = {
                    onSave(cyclesList)
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text = "Save Past Cycles ($currentAvgCycleLength days avg)",
                    fontFamily = ManropeFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(
                onClick = {
                    onSave(null) // Revert to auto-calculate
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Auto-calculate instead",
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF7A6A73)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Date Picker Modal for specific cycle
        if (activeDatePickerIndex != null) {
            val currentIndex = activeDatePickerIndex!!
            val targetDate = cyclesList.getOrNull(currentIndex)?.startDate ?: firstDayOfLastPeriod
            val initialMillis = remember(targetDate) {
                targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            }
            val datePickerState = key(currentIndex) {
                rememberDatePickerState(initialSelectedDateMillis = initialMillis)
            }

            val lumiDatePickerColors = DatePickerDefaults.colors(
                containerColor = Color(0xFFFAF7F5),
                titleContentColor = Primary,
                headlineContentColor = Primary,
                weekdayContentColor = Color(0xFF7A6A73),
                subheadContentColor = Color(0xFF5E4E57),
                yearContentColor = Color(0xFF26181F),
                currentYearContentColor = Primary,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = Primary,
                dayContentColor = Color(0xFF26181F),
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = Primary,
                todayContentColor = Primary,
                todayDateBorderColor = Primary
            )

            DatePickerDialog(
                onDismissRequest = { activeDatePickerIndex = null },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val newDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
                                val updated = cyclesList.toMutableList()
                                if (currentIndex in updated.indices) {
                                    val precedingDate = if (currentIndex == 0) firstDayOfLastPeriod else updated[currentIndex - 1].startDate
                                    val diff = ChronoUnit.DAYS.between(newDate, precedingDate).toInt()
                                    val newLen = if (diff > 0) diff.coerceIn(20, 45) else updated[currentIndex].cycleLength
                                    updated[currentIndex] = updated[currentIndex].copy(
                                        startDate = newDate,
                                        cycleLength = newLen
                                    )

                                    // Shift older cycles if not before newDate
                                    for (j in (currentIndex + 1) until updated.size) {
                                        if (!updated[j].startDate.isBefore(updated[j - 1].startDate)) {
                                            updated[j] = updated[j].copy(
                                                startDate = updated[j - 1].startDate.minusDays(updated[j].cycleLength.toLong())
                                            )
                                        }
                                    }
                                    cyclesList = updated
                                }
                            }
                            activeDatePickerIndex = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Primary)
                    ) {
                        Text("OK", fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { activeDatePickerIndex = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF7A6A73))
                    ) {
                        Text("Cancel", fontFamily = ManropeFontFamily)
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = lumiDatePickerColors
            ) {
                DatePicker(state = datePickerState, colors = lumiDatePickerColors)
            }
        }
    }
}

@Composable
private fun PastCycleInputCard(
    title: String,
    startDate: LocalDate,
    cycleLength: Int,
    periodDuration: Int,
    dateFormatter: DateTimeFormatter,
    onDateClick: () -> Unit,
    onLengthChange: (Int) -> Unit,
    onDurationChange: (Int) -> Unit,
    onRemoveClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontFamily = LiterataFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                if (onRemoveClick != null) {
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Remove cycle",
                            tint = Color(0xFF9E8791),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Start Date Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, Color(0xFFE8DFE4)), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDateClick() }
                    .background(Color(0xFFFAF7F8))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start: ${startDate.format(dateFormatter)}",
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF26181F)
                    )
                }

                Text(
                    text = "Change",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Counters Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cycle Length Counter
                MiniCounterBox(
                    label = "Cycle Length",
                    value = cycleLength,
                    onDecrease = { if (cycleLength > 20) onLengthChange(cycleLength - 1) },
                    onIncrease = { if (cycleLength < 45) onLengthChange(cycleLength + 1) },
                    modifier = Modifier.weight(1f)
                )

                // Period Duration Counter
                MiniCounterBox(
                    label = "Period Flow",
                    value = periodDuration,
                    onDecrease = { if (periodDuration > 2) onDurationChange(periodDuration - 1) },
                    onIncrease = { if (periodDuration < 10) onDurationChange(periodDuration + 1) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MiniCounterBox(
    label: String,
    value: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    unit: String = "days"
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFAF7F8))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontFamily = ManropeFontFamily,
            fontSize = 11.sp,
            color = Color(0xFF7A6A73)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onDecrease,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = Primary,
                    modifier = Modifier.size(14.dp)
                )
            }

            Text(
                text = "$value $unit",
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF26181F)
            )

            IconButton(
                onClick = onIncrease,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = Primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
