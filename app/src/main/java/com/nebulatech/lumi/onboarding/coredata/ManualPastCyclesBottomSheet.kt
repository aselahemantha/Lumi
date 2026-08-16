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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.runtime.key
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

    var cycle1Date by remember {
        mutableStateOf(
            initialPastCycles?.getOrNull(0)?.startDate
                ?: firstDayOfLastPeriod.minusDays(defaultCycleLength.toLong())
        )
    }
    var cycle1Length by remember {
        mutableIntStateOf(
            initialPastCycles?.getOrNull(0)?.cycleLength
                ?: ChronoUnit.DAYS.between(
                    initialPastCycles?.getOrNull(0)?.startDate ?: firstDayOfLastPeriod.minusDays(defaultCycleLength.toLong()),
                    firstDayOfLastPeriod
                ).toInt().coerceIn(20, 45)
        )
    }
    var cycle1Duration by remember {
        mutableIntStateOf(initialPastCycles?.getOrNull(0)?.periodDuration ?: defaultPeriodDuration)
    }

    var cycle2Date by remember {
        mutableStateOf(
            initialPastCycles?.getOrNull(1)?.startDate
                ?: firstDayOfLastPeriod.minusDays((defaultCycleLength * 2).toLong())
        )
    }
    var cycle2Length by remember {
        mutableIntStateOf(
            initialPastCycles?.getOrNull(1)?.cycleLength
                ?: ChronoUnit.DAYS.between(
                    initialPastCycles?.getOrNull(1)?.startDate ?: firstDayOfLastPeriod.minusDays((defaultCycleLength * 2).toLong()),
                    cycle1Date
                ).toInt().coerceIn(20, 45)
        )
    }
    var cycle2Duration by remember {
        mutableIntStateOf(initialPastCycles?.getOrNull(1)?.periodDuration ?: defaultPeriodDuration)
    }

    var cycle3Date by remember {
        mutableStateOf(
            initialPastCycles?.getOrNull(2)?.startDate
                ?: firstDayOfLastPeriod.minusDays((defaultCycleLength * 3).toLong())
        )
    }
    var cycle3Length by remember {
        mutableIntStateOf(
            initialPastCycles?.getOrNull(2)?.cycleLength
                ?: ChronoUnit.DAYS.between(
                    initialPastCycles?.getOrNull(2)?.startDate ?: firstDayOfLastPeriod.minusDays((defaultCycleLength * 3).toLong()),
                    cycle2Date
                ).toInt().coerceIn(20, 45)
        )
    }
    var cycle3Duration by remember {
        mutableIntStateOf(initialPastCycles?.getOrNull(2)?.periodDuration ?: defaultPeriodDuration)
    }

    val currentAvgCycleLength = round(listOf(cycle1Length, cycle2Length, cycle3Length).average()).toInt().coerceIn(20, 45)
    val currentAvgPeriodDuration = round(listOf(cycle1Duration, cycle2Duration, cycle3Duration).average()).toInt().coerceIn(2, 10)

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
                text = "Enter the start dates of your previous 3 cycles. Cycle lengths will automatically calculate based on the dates entered.",
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Color(0xFF6E5E67)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Cycle 1 Input Card
            PastCycleInputCard(
                title = "Cycle 1 (Previous Cycle)",
                startDate = cycle1Date,
                cycleLength = cycle1Length,
                periodDuration = cycle1Duration,
                dateFormatter = dateFormatter,
                onDateClick = { activeDatePickerIndex = 1 },
                onLengthChange = { newLen ->
                    cycle1Length = newLen
                    cycle1Date = firstDayOfLastPeriod.minusDays(newLen.toLong())
                },
                onDurationChange = { cycle1Duration = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Cycle 2 Input Card
            PastCycleInputCard(
                title = "Cycle 2 (2 Cycles Ago)",
                startDate = cycle2Date,
                cycleLength = cycle2Length,
                periodDuration = cycle2Duration,
                dateFormatter = dateFormatter,
                onDateClick = { activeDatePickerIndex = 2 },
                onLengthChange = { newLen ->
                    cycle2Length = newLen
                    cycle2Date = cycle1Date.minusDays(newLen.toLong())
                },
                onDurationChange = { cycle2Duration = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Cycle 3 Input Card
            PastCycleInputCard(
                title = "Cycle 3 (3 Cycles Ago)",
                startDate = cycle3Date,
                cycleLength = cycle3Length,
                periodDuration = cycle3Duration,
                dateFormatter = dateFormatter,
                onDateClick = { activeDatePickerIndex = 3 },
                onLengthChange = { newLen ->
                    cycle3Length = newLen
                    cycle3Date = cycle2Date.minusDays(newLen.toLong())
                },
                onDurationChange = { cycle3Duration = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                    text = "Calculated Average",
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
                    val list = listOf(
                        ManualPastCycle(cycle1Date, cycle1Length, cycle1Duration),
                        ManualPastCycle(cycle2Date, cycle2Length, cycle2Duration),
                        ManualPastCycle(cycle3Date, cycle3Length, cycle3Duration)
                    )
                    onSave(list)
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
            val targetDate = when (activeDatePickerIndex) {
                1 -> cycle1Date
                2 -> cycle2Date
                else -> cycle3Date
            }
            val initialMillis = remember(targetDate) {
                targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            }
            val datePickerState = key(activeDatePickerIndex) {
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
                                when (activeDatePickerIndex) {
                                    1 -> {
                                        cycle1Date = newDate
                                        val diff1 = ChronoUnit.DAYS.between(newDate, firstDayOfLastPeriod).toInt()
                                        if (diff1 > 0) {
                                            cycle1Length = diff1.coerceIn(20, 45)
                                        }
                                        if (cycle2Date.isBefore(newDate)) {
                                            val diff2 = ChronoUnit.DAYS.between(cycle2Date, newDate).toInt()
                                            if (diff2 > 0) {
                                                cycle2Length = diff2.coerceIn(20, 45)
                                            }
                                        } else {
                                            cycle2Date = newDate.minusDays(cycle2Length.toLong())
                                            cycle3Date = cycle2Date.minusDays(cycle3Length.toLong())
                                        }
                                    }
                                    2 -> {
                                        cycle2Date = newDate
                                        if (newDate.isBefore(cycle1Date)) {
                                            val diff2 = ChronoUnit.DAYS.between(newDate, cycle1Date).toInt()
                                            if (diff2 > 0) {
                                                cycle2Length = diff2.coerceIn(20, 45)
                                            }
                                        }
                                        if (cycle3Date.isBefore(newDate)) {
                                            val diff3 = ChronoUnit.DAYS.between(cycle3Date, newDate).toInt()
                                            if (diff3 > 0) {
                                                cycle3Length = diff3.coerceIn(20, 45)
                                            }
                                        } else {
                                            cycle3Date = newDate.minusDays(cycle3Length.toLong())
                                        }
                                    }
                                    3 -> {
                                        cycle3Date = newDate
                                        if (newDate.isBefore(cycle2Date)) {
                                            val diff3 = ChronoUnit.DAYS.between(newDate, cycle2Date).toInt()
                                            if (diff3 > 0) {
                                                cycle3Length = diff3.coerceIn(20, 45)
                                            }
                                        }
                                    }
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
    onDurationChange: (Int) -> Unit
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
            Text(
                text = title,
                fontFamily = LiterataFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

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
