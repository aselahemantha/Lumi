package com.nebulatech.lumi.insights.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

import com.nebulatech.lumi.insights.SymptomTrendPoint

enum class HormoneFilter {
    ALL,
    ESTROGEN,
    PROGESTERONE,
    HEADACHES
}

@Composable
fun HormoneSymptomTrendsCard(
    currentCycleDay: Int = 1,
    cycleLength: Int = 28,
    loggedSymptoms: List<SymptomTrendPoint> = emptyList(),
    modifier: Modifier = Modifier
) {
    val initialRatio = remember(currentCycleDay, cycleLength) {
        (currentCycleDay.toFloat() / cycleLength.toFloat()).coerceIn(0.05f, 0.95f)
    }
    var cursorRatio by remember(initialRatio) { mutableFloatStateOf(initialRatio) }
    var selectedFilter by remember { mutableStateOf(HormoneFilter.ALL) }
    var activeTooltip by remember { mutableStateOf<String?>(null) }

    // Resolve current phase dynamically from cursor position
    val currentPhase = when {
        cursorRatio <= 0.33f -> "Menstrual"
        cursorRatio <= 0.68f -> "Follicular"
        else -> "Luteal"
    }

    // Find nearest logged symptom to cursor or fallback
    val nearestSymptom = loggedSymptoms.minByOrNull { kotlin.math.abs(it.dayRatio - cursorRatio) }
    val loggedSymptom = when {
        nearestSymptom != null && kotlin.math.abs(nearestSymptom.dayRatio - cursorRatio) < 0.15f -> {
            "${nearestSymptom.symptomName} (Day ${nearestSymptom.cycleDay})"
        }
        cursorRatio <= 0.33f -> "Menstrual Flow"
        cursorRatio <= 0.68f -> "Estrogen Surge"
        else -> "Progesterone Peak"
    }

    val estrogenColor = Color(0xFF704257)
    val progesteroneColor = Color(0xFF5B3950)
    val headachesColor = Color(0xFFD32F2F)
    val tealNodeColor = Color(0xFF88D8C0)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "HORMONE & SYMPTOM TRENDS",
            fontFamily = ManropeFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF6E5E67),
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Current Phase",
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF7A6A73)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentPhase,
                            fontFamily = LiterataFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF4E9EE))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = loggedSymptom,
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Chart Container with Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFBF7F5))
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val r = (offset.x / size.width).coerceIn(0.05f, 0.95f)
                                    cursorRatio = r

                                    // Check node clicks
                                    val n1X = size.width * 0.42f
                                    val n2X = size.width * 0.58f
                                    if (Math.abs(offset.x - n1X) < 40f) {
                                        activeTooltip = "High Energy Logged (Day 11)"
                                    } else if (Math.abs(offset.x - n2X) < 40f) {
                                        activeTooltip = "Ovulation Window (Day 16)"
                                    } else {
                                        activeTooltip = null
                                    }
                                }
                            }
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    cursorRatio = (change.position.x / size.width).coerceIn(0.05f, 0.95f)
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height

                        val mWidth = w * 0.33f
                        val fWidth = w * 0.35f

                        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

                        // 1. Phase vertical dividers
                        drawLine(
                            color = Color(0xFFEADBDF),
                            start = Offset(mWidth, 0f),
                            end = Offset(mWidth, h * 0.82f),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = Color(0xFFEADBDF),
                            start = Offset(mWidth + fWidth, 0f),
                            end = Offset(mWidth + fWidth, h * 0.82f),
                            strokeWidth = 1.dp.toPx()
                        )

                        // 2. Interactive Cursor Line
                        val currentDayX = w * cursorRatio
                        drawLine(
                            color = Color(0xFFB0A0A8),
                            start = Offset(currentDayX, 0f),
                            end = Offset(currentDayX, h * 0.82f),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = dashEffect
                        )

                        // Estrogen Wave (Mauve)
                        val estAlpha = if (selectedFilter == HormoneFilter.ALL || selectedFilter == HormoneFilter.ESTROGEN) 1.0f else 0.25f
                        val estrogenPath = Path().apply {
                            moveTo(0f, h * 0.72f)
                            cubicTo(
                                w * 0.20f, h * 0.60f,
                                w * 0.38f, h * 0.12f,
                                w * 0.50f, h * 0.40f
                            )
                            cubicTo(
                                w * 0.65f, h * 0.70f,
                                w * 0.82f, h * 0.50f,
                                w, h * 0.75f
                            )
                        }
                        drawPath(
                            path = estrogenPath,
                            color = estrogenColor.copy(alpha = estAlpha),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Progesterone Wave (Dark Plum)
                        val progAlpha = if (selectedFilter == HormoneFilter.ALL || selectedFilter == HormoneFilter.PROGESTERONE) 1.0f else 0.25f
                        val progesteronePath = Path().apply {
                            moveTo(0f, h * 0.76f)
                            quadraticTo(
                                w * 0.45f, h * 0.73f,
                                w * 0.60f, h * 0.50f
                            )
                            cubicTo(
                                w * 0.72f, h * 0.30f,
                                w * 0.85f, h * 0.35f,
                                w, h * 0.70f
                            )
                        }
                        drawPath(
                            path = progesteronePath,
                            color = progesteroneColor.copy(alpha = progAlpha),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // 3. Teal Glowing Symptom Nodes & Connector Line (from image!)
                        val node1 = Offset(w * 0.42f, h * 0.68f)
                        val node2 = Offset(w * 0.58f, h * 0.52f)
                        val midPoint = Offset(w * 0.50f, h * 0.60f)

                        // Connecting line between nodes
                        drawLine(
                            color = tealNodeColor.copy(alpha = 0.8f),
                            start = node1,
                            end = node2,
                            strokeWidth = 1.5.dp.toPx()
                        )
                        drawCircle(color = tealNodeColor, radius = 3.dp.toPx(), center = midPoint)

                        // Glowing Node 1 (Follicular)
                        drawCircle(color = tealNodeColor.copy(alpha = 0.20f), radius = 18.dp.toPx(), center = node1)
                        drawCircle(color = tealNodeColor.copy(alpha = 0.40f), radius = 12.dp.toPx(), center = node1)
                        drawCircle(color = Color.White, radius = 4.dp.toPx(), center = node1)

                        // Glowing Node 2 (Luteal transition)
                        drawCircle(color = tealNodeColor.copy(alpha = 0.20f), radius = 18.dp.toPx(), center = node2)
                        drawCircle(color = tealNodeColor.copy(alpha = 0.40f), radius = 12.dp.toPx(), center = node2)
                        drawCircle(color = Color.White, radius = 4.dp.toPx(), center = node2)

                        // 4. Red Highlight Dot at Cursor position
                        val headAlpha = if (selectedFilter == HormoneFilter.ALL || selectedFilter == HormoneFilter.HEADACHES) 1.0f else 0.25f
                        val progY = when {
                            cursorRatio < 0.45f -> h * 0.75f
                            cursorRatio < 0.60f -> h * 0.60f
                            cursorRatio < 0.76f -> h * 0.35f
                            else -> h * 0.45f
                        }
                        val symptomPoint = Offset(currentDayX, progY)

                        // Red outer aura
                        drawCircle(
                            color = headachesColor.copy(alpha = 0.20f * headAlpha),
                            radius = 14.dp.toPx(),
                            center = symptomPoint
                        )
                        // Red inner dot
                        drawCircle(
                            color = headachesColor.copy(alpha = headAlpha),
                            radius = 5.dp.toPx(),
                            center = symptomPoint
                        )
                    }

                    // Active Tooltip Overlay
                    if (activeTooltip != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF3B2633))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = activeTooltip ?: "",
                                fontFamily = ManropeFontFamily,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }

                    // Phase X-axis Labels at bottom of chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Menstrual",
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            fontWeight = if (currentPhase == "Menstrual") FontWeight.Bold else FontWeight.Normal,
                            color = if (currentPhase == "Menstrual") Primary else Color(0xFF7A6A73)
                        )
                        Text(
                            text = "Follicular",
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            fontWeight = if (currentPhase == "Follicular") FontWeight.Bold else FontWeight.Normal,
                            color = if (currentPhase == "Follicular") Primary else Color(0xFF7A6A73)
                        )
                        Text(
                            text = "Luteal",
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            fontWeight = if (currentPhase == "Luteal") FontWeight.Bold else FontWeight.Normal,
                            color = if (currentPhase == "Luteal") Primary else Color(0xFF7A6A73)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Legend Filter Row (Tap to filter curves!)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(
                        color = estrogenColor,
                        label = "Estrogen",
                        isSelected = selectedFilter == HormoneFilter.ALL || selectedFilter == HormoneFilter.ESTROGEN,
                        onClick = {
                            selectedFilter = if (selectedFilter == HormoneFilter.ESTROGEN) HormoneFilter.ALL else HormoneFilter.ESTROGEN
                        }
                    )
                    LegendItem(
                        color = progesteroneColor,
                        label = "Progesterone",
                        isSelected = selectedFilter == HormoneFilter.ALL || selectedFilter == HormoneFilter.PROGESTERONE,
                        onClick = {
                            selectedFilter = if (selectedFilter == HormoneFilter.PROGESTERONE) HormoneFilter.ALL else HormoneFilter.PROGESTERONE
                        }
                    )
                    LegendItem(
                        color = headachesColor,
                        label = "Headaches",
                        isSelected = selectedFilter == HormoneFilter.ALL || selectedFilter == HormoneFilter.HEADACHES,
                        onClick = {
                            selectedFilter = if (selectedFilter == HormoneFilter.HEADACHES) HormoneFilter.ALL else HormoneFilter.HEADACHES
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (isSelected) color else color.copy(alpha = 0.3f))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontFamily = ManropeFontFamily,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF3B2633) else Color(0xFF9E8E96)
        )
    }
}
