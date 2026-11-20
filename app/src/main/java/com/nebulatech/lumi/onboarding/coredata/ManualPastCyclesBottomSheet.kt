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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.R
import com.nebulatech.lumi.data.model.CycleDefaults
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
import com.nebulatech.lumi.ui.theme.Background
import com.nebulatech.lumi.ui.theme.BorderSoft
import com.nebulatech.lumi.ui.theme.RoseTintMedium
import com.nebulatech.lumi.ui.theme.SurfaceWarm
import com.nebulatech.lumi.ui.theme.TextHint
import com.nebulatech.lumi.ui.theme.TextMuted
import com.nebulatech.lumi.ui.theme.TextPrimary
import com.nebulatech.lumi.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualPastCyclesBottomSheet(
    firstDayOfLastPeriod: LocalDate,
    defaultCycleLength: Int = CycleDefaults.DEFAULT_CYCLE_LENGTH,
    defaultPeriodDuration: Int = CycleDefaults.DEFAULT_PERIOD_DURATION,
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
        containerColor = Background,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.manual_past_cycles_title),
                fontFamily = LiterataFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.manual_past_cycles_desc),
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = TextMuted
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
                            text = stringResource(R.string.add_another_past_cycle),
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
                    .background(RoseTintMedium)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val cycleWord = stringResource(
                    if (cyclesList.size == 1) R.string.cycle_singular else R.string.cycle_plural
                )
                Text(
                    text = stringResource(R.string.average_cycles_count, cyclesList.size, cycleWord),
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted
                )
                Text(
                    text = stringResource(
                        R.string.average_cycle_flow_summary,
                        currentAvgCycleLength,
                        currentAvgPeriodDuration
                    ),
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
                    text = stringResource(R.string.save_past_cycles_avg, currentAvgCycleLength),
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
                    text = stringResource(R.string.auto_calculate_instead),
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted
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
                containerColor = SurfaceWarm,
                titleContentColor = Primary,
                headlineContentColor = Primary,
                weekdayContentColor = TextMuted,
                subheadContentColor = TextSecondary,
                yearContentColor = TextPrimary,
                currentYearContentColor = Primary,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = Primary,
                dayContentColor = TextPrimary,
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
                        Text(stringResource(R.string.ok_button), fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { activeDatePickerIndex = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                    ) {
                        Text(stringResource(R.string.cancel_button), fontFamily = ManropeFontFamily)
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
                            contentDescription = stringResource(R.string.remove_cycle_desc),
                            tint = TextHint,
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
                    .border(BorderStroke(1.dp, BorderSoft), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDateClick() }
                    .background(SurfaceWarm)
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
                        text = stringResource(R.string.start_date_prefix, startDate.format(dateFormatter)),
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                Text(
                    text = stringResource(R.string.change_button),
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
                    label = stringResource(R.string.cycle_length_title),
                    value = cycleLength,
                    onDecrease = { if (cycleLength > CycleDefaults.MIN_CYCLE_LENGTH) onLengthChange(cycleLength - 1) },
                    onIncrease = { if (cycleLength < CycleDefaults.MAX_CYCLE_LENGTH) onLengthChange(cycleLength + 1) },
                    modifier = Modifier.weight(1f)
                )

                // Period Duration Counter
                MiniCounterBox(
                    label = stringResource(R.string.period_flow_label),
                    value = periodDuration,
                    onDecrease = { if (periodDuration > CycleDefaults.MIN_PERIOD_DURATION) onDurationChange(periodDuration - 1) },
                    onIncrease = { if (periodDuration < CycleDefaults.MAX_PERIOD_DURATION) onDurationChange(periodDuration + 1) },
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
    unit: String = stringResource(R.string.days_unit)
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceWarm)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontFamily = ManropeFontFamily,
            fontSize = 11.sp,
            color = TextMuted
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
                    contentDescription = stringResource(R.string.decrease_desc),
                    tint = Primary,
                    modifier = Modifier.size(14.dp)
                )
            }

            Text(
                text = stringResource(R.string.value_with_unit, value, unit),
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            IconButton(
                onClick = onIncrease,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.increase_desc),
                    tint = Primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
