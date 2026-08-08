package com.nebulatech.lumi.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

// ==========================================
// 1. CYCLE RING WIDGET (Layout 2)
// ==========================================
@Composable
fun CycleRingWidget(
    cycleDay: Int = 24,
    subLabelText: String = "Period starts in ~4 days",
    progressRatio: Float = 0.85f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(240.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val strokeWidth = 16.dp.toPx()
            // Track background arc
            drawArc(
                color = Color(0xFFF3EAEF),
                startAngle = -220f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            // Progress arc
            drawArc(
                color = Primary,
                startAngle = -220f,
                sweepAngle = 260f * progressRatio,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CYCLE DAY",
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = Color(0xFF6B5861)
            )
            Text(
                text = "$cycleDay",
                fontFamily = LiterataFontFamily,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Text(
                text = subLabelText,
                fontFamily = ManropeFontFamily,
                fontSize = 15.sp,
                color = Color(0xFF4A3B43),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// 2. LUMI INSIGHT CARD (White Standard Variant)
// ==========================================
@Composable
fun LumiInsightCard(
    title: String = "Lumi Insight",
    text: String = "Your last 3 cycles have varied by 8 days. To help stabilize ovulation this week, try swapping high-intensity workouts for yoga.",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFBF0F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontFamily = LiterataFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = text,
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFF4E4047)
                )
            }
        }
    }
}

// ==========================================
// 3. NEXT 7 DAYS CALENDAR STRIP (Layout 2)
// ==========================================
data class DayItem(
    val dayName: String,
    val dateNumber: Int,
    val isToday: Boolean = false,
    val hasPeriodDot: Boolean = false
)

@Composable
fun Next7DaysCalendarStrip(
    days: List<DayItem> = listOf(
        DayItem("Mon", 12),
        DayItem("Tue", 13, isToday = true),
        DayItem("Wed", 14),
        DayItem("Thu", 15),
        DayItem("Fri", 16, hasPeriodDot = true),
        DayItem("Sat", 17, hasPeriodDot = true),
        DayItem("Sun", 18, hasPeriodDot = true)
    ),
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "NEXT 7 DAYS",
            fontFamily = ManropeFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = Color(0xFF6E5D66),
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                days.forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = day.dayName,
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            color = if (day.isToday) Primary else Color(0xFF7A6B73)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (day.isToday) Primary else Color.Transparent)
                                .border(
                                    width = if (day.hasPeriodDot && !day.isToday) 1.dp else 0.dp,
                                    color = if (day.hasPeriodDot) Primary.copy(alpha = 0.5f) else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${day.dateNumber}",
                                fontFamily = ManropeFontFamily,
                                fontSize = 16.sp,
                                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                                color = if (day.isToday) Color.White else Color(0xFF3B2E35)
                            )
                        }
                        if (day.hasPeriodDot) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(Primary)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. LATE LUTEAL HEADER CARD (Layout 1)
// ==========================================
@Composable
fun LateLutealHeaderCard(
    dayNumber: Int = 24,
    title: String = "Late Luteal Phase",
    description: String = "Progesterone is dropping. You may notice shifts in energy and mood.",
    progressRatio: Float = 0.85f,
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF7DDE6))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "DAY $dayNumber",
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontFamily = LiterataFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF26181F),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                color = Color(0xFF594852),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Primary,
                trackColor = Color(0xFFEBE3E7)
            )
        }
    }
}

// ==========================================
// 5. SYMPTOM GRID ITEM & SECTION (Layout 1)
// ==========================================
data class SymptomCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val isSelected: Boolean = false,
    val isCustom: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogSymptomsSection(
    symptoms: List<SymptomCategory> = listOf(
        SymptomCategory("energy", "Energy", Icons.Outlined.Spa),
        SymptomCategory("mood", "Mood", Icons.Outlined.SentimentDissatisfied, isSelected = true),
        SymptomCategory("skin", "Skin", Icons.Outlined.Face),
        SymptomCategory("digestion", "Digestion", Icons.Outlined.Restaurant),
        SymptomCategory("pain", "Pain", Icons.Outlined.Healing),
        SymptomCategory("add", "Add Custom", Icons.Outlined.Add, isCustom = true)
    ),
    onSymptomToggle: (SymptomCategory) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Log Today's Symptoms",
                fontFamily = LiterataFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF26181F)
            )
            Text(
                text = "View All",
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        // 2 Column Grid using FlowRow
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            symptoms.forEach { symptom ->
                val cardColor = when {
                    symptom.isSelected -> Primary
                    symptom.isCustom -> Color(0xFFEFECE9)
                    else -> Color.White
                }
                val contentColor = if (symptom.isSelected) Color.White else Color(0xFF2E2027)

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(76.dp)
                        .clickable { onSymptomToggle(symptom) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (symptom.isCustom) 0.dp else 1.dp),
                    border = if (symptom.isCustom) BorderStroke(1.dp, Color(0xFFD6CECA)) else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = symptom.icon,
                            contentDescription = symptom.name,
                            tint = contentColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = symptom.name,
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = contentColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Daily Log",
                    fontFamily = ManropeFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// 6. LUMI INSIGHT PINK CARD (Layout 1)
// ==========================================
@Composable
fun LumiInsightPinkCard(
    insightText: String = "You frequently log migraines around Day 24. This is common when estrogen drops.",
    actionText: String = "Action: Try increasing magnesium intake today.",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCDCE8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Spa,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "LUMI INSIGHT",
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color(0xFF6B5560)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = insightText,
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color(0xFF382A31)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Medication,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = actionText,
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF261920)
                    )
                }
            }
        }
    }
}

// ==========================================
// 7. THIRTY DAY TRENDS CARD (Layout 1)
// ==========================================
@Composable
fun ThirtyDayTrendsCard(
    mostFrequentSymptom: String = "Headaches",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "30-Day Trends",
                    fontFamily = LiterataFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF26181F)
                )
                Icon(
                    imageVector = Icons.Outlined.BarChart,
                    contentDescription = null,
                    tint = Color(0xFF36282E),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "MOST FREQUENT",
                fontFamily = ManropeFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color(0xFF73636B)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = mostFrequentSymptom,
                fontFamily = ManropeFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF26181F)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Mini bar representation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text("Week 1", fontFamily = ManropeFontFamily, fontSize = 11.sp, color = Color(0xFF8A7A83))
                Text("W3", fontFamily = ManropeFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                Text("Week 4", fontFamily = ManropeFontFamily, fontSize = 11.sp, color = Color(0xFF8A7A83))
            }
        }
    }
}

// ==========================================
// 8. FERTILITY HEADER CARD (Layout 3)
// ==========================================
@Composable
fun FertilityHeaderCard(
    statusTag: String = "CURRENT STATUS",
    title: String = "High Fertility Today",
    description: String = "Ovulation expected tomorrow. This is your peak window.",
    onLogBBTClick: () -> Unit = {},
    onLogLHClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF0F4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF7D5E1))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = statusTag,
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Primary
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                fontFamily = LiterataFontFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                color = Color(0xFF4A3B43),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Stack of buttons
            Button(
                onClick = onLogBBTClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Thermostat,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log BBT",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onLogLHClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                border = BorderStroke(1.dp, Primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Biotech,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log LH Result",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ==========================================
// 9. BASAL BODY TEMP CHART CARD (Layout 3)
// ==========================================
@Composable
fun BasalBodyTempChartCard(
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
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Basal Body Temp",
                    fontFamily = LiterataFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF26181F)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF7DDE6))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "This Cycle",
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart area representation using Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Grid lines & Y Axis labels
                    val yLines = listOf(0.2f, 0.5f, 0.8f)
                    yLines.forEach { ratio ->
                        drawLine(
                            color = Color(0xFFF0E8EC),
                            start = Offset(0f, height * ratio),
                            end = Offset(width, height * ratio),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Line Chart Path
                    val path = Path().apply {
                        moveTo(width * 0.1f, height * 0.72f)
                        quadraticTo(
                            width * 0.25f, height * 0.7f,
                            width * 0.35f, height * 0.68f
                        )
                        quadraticTo(
                            width * 0.5f, height * 0.65f,
                            width * 0.65f, height * 0.67f
                        )
                        cubicTo(
                            width * 0.72f, height * 0.40f,
                            width * 0.76f, height * 0.15f,
                            width * 0.80f, height * 0.20f
                        )
                        quadraticTo(
                            width * 0.88f, height * 0.30f,
                            width * 0.95f, height * 0.28f
                        )
                    }

                    drawPath(
                        path = path,
                        color = Primary,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw dots along points
                    val points = listOf(
                        Offset(width * 0.12f, height * 0.72f),
                        Offset(width * 0.28f, height * 0.70f),
                        Offset(width * 0.42f, height * 0.67f),
                        Offset(width * 0.54f, height * 0.65f),
                        Offset(width * 0.66f, height * 0.67f)
                    )

                    points.forEach { pt ->
                        drawCircle(color = Color.White, radius = 5.dp.toPx(), center = pt)
                        drawCircle(color = Primary, radius = 5.dp.toPx(), center = pt, style = Stroke(width = 2.dp.toPx()))
                    }

                    // Highlighted peak point CD16
                    val peakPt = Offset(width * 0.78f, height * 0.24f)
                    drawCircle(color = Primary, radius = 6.dp.toPx(), center = peakPt)
                }

                // Callout for 98.1° tag
                Box(
                    modifier = Modifier
                        .padding(start = 190.dp, top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2D6DC), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "98.1°",
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF26181F)
                    )
                }
            }

            // X-Axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("CD10", "CD12", "CD14", "CD16", "CD18").forEach { cd ->
                    Text(
                        text = cd,
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        fontWeight = if (cd == "CD16") FontWeight.Bold else FontWeight.Normal,
                        color = if (cd == "CD16") Primary else Color(0xFF8C7C85)
                    )
                }
            }
        }
    }
}

// ==========================================
// 10. TODAY'S LOGS CARD (Layout 3)
// ==========================================
data class LogEntryItem(
    val icon: ImageVector,
    val title: String,
    val category: String
)

@Composable
fun TodaysLogsCard(
    logs: List<LogEntryItem> = listOf(
        LogEntryItem(Icons.Outlined.WaterDrop, "Egg White", "Cervical Mucus"),
        LogEntryItem(Icons.Outlined.SentimentSatisfied, "Energetic, Calm", "Mood")
    ),
    onAddMoreLogsClick: () -> Unit = {},
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
                .padding(20.dp)
        ) {
            Text(
                text = "TODAY'S LOGS",
                fontFamily = ManropeFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color(0xFF6E5E67)
            )
            Spacer(modifier = Modifier.height(14.dp))

            logs.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = item.title,
                            fontFamily = ManropeFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF26181F)
                        )
                        Text(
                            text = item.category,
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF82727B)
                        )
                    }
                }
                if (index < logs.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "+ Add More Logs",
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onAddMoreLogsClick() }
            )
        }
    }
}

// ==========================================
// 11. LIBRARY FEATURED CARD (Layout 3)
// ==========================================
@Composable
fun LibraryFeaturedCard(
    title: String = "Understanding LH Surges",
    onReadArticleClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LIBRARY",
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color(0xFFE8D4DD)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    fontFamily = LiterataFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.clickable { onReadArticleClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Read article",
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.25f),
                modifier = Modifier.size(64.dp)
            )
        }
    }
}
