package com.nebulatech.lumi.insights.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

data class HormoneLevels(
    val estrogen: Float,     // pg/mL
    val progesterone: Float, // ng/mL
    val lh: Float,           // mIU/mL
    val fsh: Float,          // mIU/mL
    val phaseName: String
)

fun calculateHormonesForDay(day: Float): HormoneLevels {
    val d = day.coerceIn(1f, 28f)

    // Estrogen curve (pg/mL)
    val estrogen = when {
        d < 8f -> 50f
        d in 8f..13f -> 50f + 290f * Math.sin(((d - 8f) / 5f) * (Math.PI / 2)).toFloat()
        d in 13f..16f -> 340f - 250f * Math.sin(((d - 13f) / 3f) * (Math.PI / 2)).toFloat()
        d in 16f..22f -> 90f + 100f * Math.sin(((d - 16f) / 6f) * (Math.PI / 2)).toFloat()
        else -> 190f - 140f * ((d - 22f) / 6f)
    }.coerceIn(20f, 350f)

    // Progesterone curve (ng/mL)
    val progesterone = when {
        d < 13f -> 0.5f
        d in 13f..21f -> 0.5f + 17.5f * Math.sin(((d - 13f) / 8f) * (Math.PI / 2)).toFloat()
        else -> 18.0f - 17.5f * ((d - 21f) / 7f)
    }.coerceIn(0.5f, 20.0f)

    // LH surge (mIU/mL)
    val lh = when {
        d in 11.5f..15.5f -> 5f + 55f * Math.exp(-Math.pow((d - 13.5) / 0.8, 2.0)).toFloat()
        else -> 5f
    }.coerceIn(5f, 65f)

    // FSH surge (mIU/mL x2 scale)
    val fsh = when {
        d in 11.5f..15.5f -> 8f + 27f * Math.exp(-Math.pow((d - 13.5) / 0.9, 2.0)).toFloat()
        else -> 8f
    }.coerceIn(5f, 35f)

    val phaseName = when {
        d <= 5.5f -> "Menstruation"
        d <= 12.5f -> "Follicular"
        d <= 15.5f -> "Ovulation"
        else -> "Luteal"
    }

    return HormoneLevels(estrogen, progesterone, lh, fsh, phaseName)
}

@Composable
fun InteractiveHormoneChartCard(
    initialDay: Int = 1,
    modifier: Modifier = Modifier
) {
    var selectedDay by remember(initialDay) { mutableFloatStateOf(initialDay.toFloat().coerceIn(1f, 28f)) }
    val currentHormones = remember(selectedDay) { calculateHormonesForDay(selectedDay) }

    val estrogenColor = Color(0xFF1E65D8)     // Blue
    val progesteroneColor = Color(0xFF1B7D32) // Green
    val lhColor = Color(0xFFE67E22)           // Yellow / Orange
    val fshColor = Color(0xFFD32F2F)          // Red

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
            // Chart Title
            Text(
                text = "Hormone Cycle Tracker",
                fontFamily = LiterataFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF26181F)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Y-Axis Scale Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level (Relative Intensity) ↑",
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF7A6A73)
                )

                Text(
                    text = "Cycle Day →",
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF7A6A73)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Interactive Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val chartLeft = size.width * 0.12f
                                val chartRight = size.width * 0.92f
                                val chartWidth = chartRight - chartLeft
                                if (chartWidth > 0) {
                                    val dayRatio = ((offset.x - chartLeft) / chartWidth).coerceIn(0f, 1f)
                                    selectedDay = 1f + dayRatio * 27f
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                val chartLeft = size.width * 0.12f
                                val chartRight = size.width * 0.92f
                                val chartWidth = chartRight - chartLeft
                                if (chartWidth > 0) {
                                    val dayRatio = ((change.position.x - chartLeft) / chartWidth).coerceIn(0f, 1f)
                                    selectedDay = 1f + dayRatio * 27f
                                }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    val paddingLeft = w * 0.12f
                    val paddingRight = w * 0.08f
                    val paddingTop = h * 0.08f
                    val paddingBottom = h * 0.22f

                    val chartW = w - paddingLeft - paddingRight
                    val chartH = h - paddingTop - paddingBottom

                    fun dayToX(day: Float): Float = paddingLeft + ((day - 1f) / 27f) * chartW
                    fun valToY(value: Float, maxVal: Float = 400f): Float = paddingTop + (1f - (value / maxVal)) * chartH

                    // 1. Ovulation Phase Highlight Band (Days 13 to 15)
                    val ovulationX1 = dayToX(13f)
                    val ovulationX2 = dayToX(15f)
                    drawRect(
                        color = Color(0xFFEBF3FC),
                        topLeft = Offset(ovulationX1, paddingTop),
                        size = Size(ovulationX2 - ovulationX1, chartH)
                    )

                    // 2. Horizontal Grid Lines (400, 300, 200, 100, 0)
                    val yLevels = listOf(0, 100, 200, 300, 400)
                    yLevels.forEach { lvl ->
                        val y = valToY(lvl.toFloat())
                        drawLine(
                            color = Color(0xFFECE6E8),
                            start = Offset(paddingLeft, y),
                            end = Offset(w - paddingRight, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // 3. Vertical Phase Grid Lines
                    val phaseDays = listOf(5.5f, 12.5f, 15.5f)
                    phaseDays.forEach { pd ->
                        val x = dayToX(pd)
                        drawLine(
                            color = Color(0xFFE4DCDD),
                            start = Offset(x, paddingTop),
                            end = Offset(x, paddingTop + chartH),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // 4. Generate & Draw 4 Hormone Curves
                    val stepCount = 56
                    val estrogenPoints = mutableListOf<Offset>()
                    val progesteronePoints = mutableListOf<Offset>()
                    val lhPoints = mutableListOf<Offset>()
                    val fshPoints = mutableListOf<Offset>()

                    for (i in 0..stepCount) {
                        val d = 1f + (i.toFloat() / stepCount) * 27f
                        val hData = calculateHormonesForDay(d)
                        val x = dayToX(d)

                        estrogenPoints.add(Offset(x, valToY(hData.estrogen)))
                        // Scale progesterone (ng/mL x10) for relative plot
                        progesteronePoints.add(Offset(x, valToY(hData.progesterone * 10f)))
                        lhPoints.add(Offset(x, valToY(hData.lh)))
                        // Scale FSH (mIU/mL x2)
                        fshPoints.add(Offset(x, valToY(hData.fsh * 2f)))
                    }

                    fun createSmoothPath(points: List<Offset>): Path = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points[0].x, points[0].y)
                            for (i in 0 until points.size - 1) {
                                val p1 = points[i]
                                val p2 = points[i + 1]
                                val cx = (p1.x + p2.x) / 2f
                                val cy = (p1.y + p2.y) / 2f
                                quadraticTo(p1.x, p1.y, cx, cy)
                            }
                            lineTo(points.last().x, points.last().y)
                        }
                    }

                    // Draw Curves
                    drawPath(createSmoothPath(estrogenPoints), estrogenColor, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
                    drawPath(createSmoothPath(progesteronePoints), progesteroneColor, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
                    drawPath(createSmoothPath(lhPoints), lhColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                    drawPath(createSmoothPath(fshPoints), fshColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))

                    // 5. Vertical Selected Day Line (Dashed)
                    val cursorX = dayToX(selectedDay)
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    drawLine(
                        color = Color(0xFF26181F),
                        start = Offset(cursorX, paddingTop - 8.dp.toPx()),
                        end = Offset(cursorX, paddingTop + chartH + 4.dp.toPx()),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = dashEffect
                    )

                    // 6. Color Dots on Curves at Cursor X Position
                    val selHormones = calculateHormonesForDay(selectedDay)
                    val estY = valToY(selHormones.estrogen)
                    val progY = valToY(selHormones.progesterone * 10f)
                    val lhY = valToY(selHormones.lh)
                    val fshY = valToY(selHormones.fsh * 2f)

                    drawCircle(estrogenColor, radius = 4.dp.toPx(), center = Offset(cursorX, estY))
                    drawCircle(progesteroneColor, radius = 4.dp.toPx(), center = Offset(cursorX, progY))
                    drawCircle(lhColor, radius = 4.dp.toPx(), center = Offset(cursorX, lhY))
                    drawCircle(fshColor, radius = 4.dp.toPx(), center = Offset(cursorX, fshY))
                }

                // Overlay Axis Labels & Phase Labels
                // Y-Axis Ticks (400, 300, 200, 100, 0)
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.78f)
                        .align(Alignment.TopStart),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("400", "300", "200", "100", "0").forEach { tick ->
                        Text(
                            text = tick,
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.sp,
                            color = Color(0xFF8A7A83)
                        )
                    }
                }

                // X-Axis Day Numbers (1, 5, 10, 14, 20, 25, 28)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.80f)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("1", "5", "10", "14", "20", "25", "28").forEach { dayLabel ->
                        Text(
                            text = dayLabel,
                            fontFamily = ManropeFontFamily,
                            fontSize = 11.sp,
                            fontWeight = if (dayLabel == "${selectedDay.toInt()}") FontWeight.Bold else FontWeight.Normal,
                            color = if (dayLabel == "${selectedDay.toInt()}") Primary else Color(0xFF7A6A73)
                        )
                    }
                }

                // Phase Labels Below Chart (Menstrual, Follicular, Ovulation, Luteal)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Menstrual", fontFamily = ManropeFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6E5E67))
                    Text("Follicular", fontFamily = ManropeFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6E5E67))
                    Text("Ovulation", fontFamily = ManropeFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E65D8))
                    Text("Luteal", fontFamily = ManropeFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6E5E67))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hormone Color Legend Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HormoneLegendItem(color = estrogenColor, label = "Estrogen (pg/mL)")
                HormoneLegendItem(color = progesteroneColor, label = "Progesterone (ng/mL x10)")
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HormoneLegendItem(color = lhColor, label = "LH (mIU/mL)")
                HormoneLegendItem(color = fshColor, label = "FSH (mIU/mL x2)")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF2ECEF))
            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Value Dashboard Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Column 1: PHASE
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PHASE",
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF7A6A73)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentHormones.phaseName,
                        fontFamily = LiterataFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF26181F)
                    )
                }

                // Vertical Line
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(Color(0xFFEADBDF))
                )

                // Column 2: ESTROGEN
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ESTROGEN",
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF7A6A73)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${currentHormones.estrogen.toInt()} pg/mL",
                        fontFamily = LiterataFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF26181F)
                    )
                }

                // Vertical Line
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(Color(0xFFEADBDF))
                )

                // Column 3: PROGESTERONE
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PROGESTERONE",
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF7A6A73)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("%.1f ng/mL", currentHormones.progesterone),
                        fontFamily = LiterataFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF26181F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Interactive Day Slider Control
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Day of Cycle",
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF3B2D34)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Slider(
                    value = selectedDay,
                    onValueChange = { selectedDay = it },
                    valueRange = 1f..28f,
                    steps = 26,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF26181F),
                        activeTrackColor = Primary,
                        inactiveTrackColor = Color(0xFFEADBDF)
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Day Value Pill Box
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF5F3F1))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${selectedDay.toInt()}",
                        fontFamily = LiterataFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF26181F)
                    )
                }
            }
        }
    }
}

@Composable
private fun HormoneLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontFamily = ManropeFontFamily,
            fontSize = 11.sp,
            color = Color(0xFF5E4E57)
        )
    }
}
