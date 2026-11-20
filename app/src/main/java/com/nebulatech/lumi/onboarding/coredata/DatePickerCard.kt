package com.nebulatech.lumi.onboarding.coredata

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.R
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import com.nebulatech.lumi.ui.theme.BorderSoft
import com.nebulatech.lumi.ui.theme.DividerSubtle
import com.nebulatech.lumi.ui.theme.RoseTintLight
import com.nebulatech.lumi.ui.theme.SurfaceWarm
import com.nebulatech.lumi.ui.theme.TextMuted
import com.nebulatech.lumi.ui.theme.TextPrimary
import com.nebulatech.lumi.ui.theme.TextSecondary
import com.nebulatech.lumi.ui.theme.TextSubtle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerCard(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    periodDuration: Int = 5
) {
    val showDatePicker = remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.last_period_title),
                fontFamily = LiterataFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.last_period_subtitle),
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Date picker button trigger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.dp, BorderSoft),
                        RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showDatePicker.value = true }
                    .background(SurfaceWarm)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = selectedDate.format(dateFormatter),
                        fontFamily = ManropeFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSubtle,
                    modifier = Modifier.size(20.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = DividerSubtle
            )

            // Horizontal calendar strip highlighting the entire period duration
            val startOfWeek = getStartOfWeek(selectedDate)
            val weekDays = remember(startOfWeek) {
                (0..6).map { startOfWeek.plusDays(it.toLong()) }
            }
            val weekdayLabels = listOf("S", "M", "T", "W", "T", "F", "S")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekDays.forEachIndexed { index, day ->
                    val isPeriodStart = day.isEqual(selectedDate)
                    val isPeriodDay = !day.isBefore(selectedDate) &&
                            day.isBefore(selectedDate.plusDays(periodDuration.toLong()))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = weekdayLabels[index],
                            fontFamily = ManropeFontFamily,
                            fontSize = 11.sp,
                            color = when {
                                isPeriodStart -> Primary
                                isPeriodDay -> Primary.copy(alpha = 0.85f)
                                else -> TextSubtle
                            },
                            fontWeight = if (isPeriodDay) FontWeight.Bold else FontWeight.Normal
                        )

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { onDateSelected(day) }
                                .background(
                                    color = when {
                                        isPeriodStart -> Primary
                                        isPeriodDay -> RoseTintLight
                                        else -> SurfaceWarm
                                    }
                                )
                                .then(
                                    when {
                                        isPeriodStart -> Modifier
                                        isPeriodDay -> Modifier.border(
                                            BorderStroke(1.dp, Primary.copy(alpha = 0.35f)),
                                            CircleShape
                                        )
                                        else -> Modifier.border(
                                            BorderStroke(1.dp, BorderSoft),
                                            CircleShape
                                        )
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.dayOfMonth.toString(),
                                fontFamily = ManropeFontFamily,
                                fontSize = 13.sp,
                                color = when {
                                    isPeriodStart -> Color.White
                                    isPeriodDay -> Primary
                                    else -> TextPrimary
                                },
                                fontWeight = if (isPeriodDay) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }

    // Material 3 Date Picker Dialog with Lumi Theme Colors
    if (showDatePicker.value) {
        val initialSelectedMillis = remember(selectedDate) {
            selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedMillis
        )

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
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            onDateSelected(newDate)
                        }
                        showDatePicker.value = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Primary)
                ) {
                    Text(
                        text = stringResource(R.string.ok_button),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker.value = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                ) {
                    Text(
                        text = stringResource(R.string.cancel_button),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            },
            shape = RoundedCornerShape(28.dp),
            colors = lumiDatePickerColors
        ) {
            DatePicker(
                state = datePickerState,
                colors = lumiDatePickerColors
            )
        }
    }
}

private fun getStartOfWeek(date: LocalDate): LocalDate {
    val dayOfWeek = date.dayOfWeek
    return if (dayOfWeek == java.time.DayOfWeek.SUNDAY) {
        date
    } else {
        date.minusDays(dayOfWeek.value.toLong())
    }
}
