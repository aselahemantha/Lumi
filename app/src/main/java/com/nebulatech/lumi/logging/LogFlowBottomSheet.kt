package com.nebulatech.lumi.logging

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.analytics.AnalyticsConstants
import com.nebulatech.lumi.analytics.LocalAnalyticsTracker
import com.nebulatech.lumi.analytics.TrackScreenView
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

enum class FlowIntensity(val label: String) {
    LIGHT("Light"),
    MEDIUM("Medium"),
    HEAVY("Heavy")
}

enum class MoodItem(val label: String, val icon: ImageVector) {
    CALM("Calm", Icons.Outlined.SentimentSatisfied),
    ENERGETIC("Energetic", Icons.Outlined.Bolt),
    SENSITIVE("Sensitive", Icons.Outlined.WaterDrop),
    TIRED("Tired", Icons.Outlined.ModeNight)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LogFlowBottomSheet(
    onDismissRequest: () -> Unit,
    initialDate: LocalDate = LocalDate.now(),
    onSaveLog: (LocalDate, FlowIntensity?, Set<String>, MoodItem?) -> Unit = { _, _, _, _ -> },
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    modifier: Modifier = Modifier
) {
    val tracker = LocalAnalyticsTracker.current
    TrackScreenView(AnalyticsConstants.Screens.DAILY_LOG)

    var selectedDate by remember(initialDate) { mutableStateOf(initialDate) }
    val showDatePicker = remember { mutableStateOf(false) }
    var selectedFlow by remember { mutableStateOf<FlowIntensity?>(FlowIntensity.MEDIUM) }
    var selectedSymptoms by remember { mutableStateOf(emptySet<String>()) }
    var selectedMood by remember { mutableStateOf<MoodItem?>(null) }
    var isMoreDetailsExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFDCD2D6))
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Header Row: Daily Log + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Log",
                    fontFamily = LiterataFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF26181F)
                )

                IconButton(
                    onClick = {
                        tracker.trackButtonClick(
                            buttonName = AnalyticsConstants.Buttons.CANCEL_DAILY_LOG,
                            screenName = AnalyticsConstants.Screens.DAILY_LOG
                        )
                        onDismissRequest()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF3B2D34),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date Selection Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log Date",
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B2D34)
                )

                val displayDateText = remember(selectedDate) {
                    val today = LocalDate.now()
                    val yesterday = today.minusDays(1)
                    val tomorrow = today.plusDays(1)
                    when {
                        selectedDate.isEqual(today) -> "Today, ${selectedDate.format(DateTimeFormatter.ofPattern("MMM d"))}"
                        selectedDate.isEqual(yesterday) -> "Yesterday, ${selectedDate.format(DateTimeFormatter.ofPattern("MMM d"))}"
                        selectedDate.isEqual(tomorrow) -> "Tomorrow, ${selectedDate.format(DateTimeFormatter.ofPattern("MMM d"))}"
                        else -> selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy"))
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF7F2F4))
                        .border(
                            BorderStroke(1.dp, Color(0xFFEADBDF)),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { showDatePicker.value = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Date",
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = displayDateText,
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF26181F)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF8A7A83),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFF2ECEF)
            )

            // Section 1: Flow Intensity
            Text(
                text = "Flow Intensity",
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B2D34)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FlowIntensity.entries.forEach { flow ->
                    val isSelected = selectedFlow == flow
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clickable { selectedFlow = flow },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFFDF2F6) else Color(0xFFF5F3F1)
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Primary) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Dot representation
                            when (flow) {
                                FlowIntensity.LIGHT -> {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF7D5E1)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Primary)
                                        )
                                    }
                                }
                                FlowIntensity.MEDIUM -> {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF7D5E1)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(Primary)
                                        )
                                    }
                                }
                                FlowIntensity.HEAVY -> {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Primary)
                                        )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = flow.label,
                                fontFamily = ManropeFontFamily,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Primary else Color(0xFF3B2D34)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Expandable Accordion for Symptoms & Mood (Option C)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { isMoreDetailsExpanded = !isMoreDetailsExpanded },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isMoreDetailsExpanded || selectedSymptoms.isNotEmpty() || selectedMood != null) {
                        Color(0xFFFDF8FA)
                    } else {
                        Color(0xFFFAF7F8)
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    if (selectedSymptoms.isNotEmpty() || selectedMood != null) Primary.copy(alpha = 0.3f) else Color(0xFFEADBDF)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Symptoms & Mood",
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF26181F)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(Optional)",
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 12.sp,
                                    color = Color(0xFF8A7A83)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when {
                                    selectedSymptoms.isEmpty() && selectedMood == null -> "Tap to log cramps, mood or energy"
                                    selectedSymptoms.isNotEmpty() && selectedMood != null -> "${selectedSymptoms.size} symptoms • ${selectedMood?.label}"
                                    selectedSymptoms.isNotEmpty() -> "${selectedSymptoms.size} symptoms selected"
                                    else -> "Mood: ${selectedMood?.label}"
                                },
                                fontFamily = ManropeFontFamily,
                                fontSize = 12.sp,
                                color = if (selectedSymptoms.isNotEmpty() || selectedMood != null) Primary else Color(0xFF8A7A83)
                            )
                        }

                        Icon(
                            imageVector = if (isMoreDetailsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isMoreDetailsExpanded) "Collapse" else "Expand",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    AnimatedVisibility(
                        visible = isMoreDetailsExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            HorizontalDivider(color = Color(0xFFF2ECEF))

                            Spacer(modifier = Modifier.height(14.dp))

                            // Symptoms Selection
                            Text(
                                text = "Symptoms",
                                fontFamily = ManropeFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3B2D34)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val availableSymptoms = listOf("Cramps", "Bloating", "Headache", "Fatigue", "Backache")
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                availableSymptoms.forEach { symptom ->
                                    val isSelected = selectedSymptoms.contains(symptom)
                                    val chipBg = if (isSelected) Primary else Color.White
                                    val textColor = if (isSelected) Color.White else Color(0xFF4A3B43)
                                    val border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFEADBDF))

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(chipBg)
                                            .then(if (border != null) Modifier.border(border, RoundedCornerShape(16.dp)) else Modifier)
                                            .clickable {
                                                selectedSymptoms = if (isSelected) {
                                                    selectedSymptoms - symptom
                                                } else {
                                                    selectedSymptoms + symptom
                                                }
                                            }
                                            .padding(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = symptom,
                                            fontFamily = ManropeFontFamily,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = textColor
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Mood Selection
                            Text(
                                text = "Mood",
                                fontFamily = ManropeFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3B2D34)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                MoodItem.entries.forEach { mood ->
                                    val isSelected = selectedMood == mood
                                    val circleBg = if (isSelected) Primary else Color.White
                                    val iconColor = if (isSelected) Color.White else Color(0xFF3B2D34)

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.clickable {
                                            selectedMood = if (isSelected) null else mood
                                        }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(circleBg)
                                                .border(
                                                    BorderStroke(1.dp, if (isSelected) Primary else Color(0xFFEADBDF)),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = mood.icon,
                                                contentDescription = mood.label,
                                                tint = iconColor,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = mood.label,
                                            fontFamily = ManropeFontFamily,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Primary else Color(0xFF4A3B43)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Log Button
            Button(
                onClick = {
                    tracker.trackButtonClick(
                        buttonName = AnalyticsConstants.Buttons.SAVE_DAILY_LOG,
                        screenName = AnalyticsConstants.Screens.DAILY_LOG,
                        extraParams = mapOf(
                            "date" to selectedDate.toString(),
                            "flow" to (selectedFlow?.name ?: "none"),
                            "mood" to (selectedMood?.name ?: "none"),
                            "symptoms_count" to selectedSymptoms.size
                        )
                    )
                    tracker.trackEvent(
                        AnalyticsConstants.Events.LOG_SAVED,
                        mapOf(
                            "date" to selectedDate.toString(),
                            "flow" to (selectedFlow?.name ?: "none"),
                            "mood" to (selectedMood?.name ?: "none"),
                            "symptoms_count" to selectedSymptoms.size
                        )
                    )
                    onSaveLog(selectedDate, selectedFlow, selectedSymptoms, selectedMood)
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text = "Save Log",
                    fontFamily = ManropeFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDatePicker.value) {
        val initialSelectedMillis = remember(selectedDate) {
            selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedMillis
        )

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
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            selectedDate = newDate
                        }
                        showDatePicker.value = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Primary)
                ) {
                    Text(
                        text = "OK",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker.value = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF7A6A73))
                ) {
                    Text(
                        text = "Cancel",
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
