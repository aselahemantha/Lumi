package com.nebulatech.lumi.insights.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

@Composable
fun HormoneSymptomTrendsCard(
    currentPhase: String = "Luteal",
    loggedSymptom: String = "Headaches logged",
    modifier: Modifier = Modifier
) {
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

                Spacer(modifier = Modifier.height(16.dp))

                // Chart Container with Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF7F5F3))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Phase vertical shading & dashed dividers
                        val mWidth = w * 0.33f
                        val fWidth = w * 0.35f

                        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

                        // Vertical dashed divider 1 (between Menstrual & Follicular)
                        drawLine(
                            color = Color(0xFFE4DCDD),
                            start = Offset(mWidth, 0f),
                            end = Offset(mWidth, h * 0.85f),
                            strokeWidth = 1.dp.toPx()
                        )

                        // Vertical dashed divider 2 (between Follicular & Luteal)
                        drawLine(
                            color = Color(0xFFE4DCDD),
                            start = Offset(mWidth + fWidth, 0f),
                            end = Offset(mWidth + fWidth, h * 0.85f),
                            strokeWidth = 1.dp.toPx()
                        )

                        // Current day vertical line (dashed)
                        val currentDayX = w * 0.76f
                        drawLine(
                            color = Color(0xFFB0A0A8),
                            start = Offset(currentDayX, 0f),
                            end = Offset(currentDayX, h * 0.85f),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = dashEffect
                        )

                        // Estrogen Wave (Mauve curve - surges in Follicular, small secondary bump in Luteal)
                        val estrogenColor = Color(0xFF704257)
                        val estrogenPath = Path().apply {
                            moveTo(0f, h * 0.72f)
                            cubicTo(
                                w * 0.20f, h * 0.60f,
                                w * 0.35f, h * 0.10f,
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
                            color = estrogenColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Progesterone Wave (Purple curve - surges high in Luteal)
                        val progesteroneColor = Color(0xFF5B3950)
                        val progesteronePath = Path().apply {
                            moveTo(0f, h * 0.75f)
                            quadraticTo(
                                w * 0.45f, h * 0.73f,
                                w * 0.60f, h * 0.50f
                            )
                            cubicTo(
                                w * 0.70f, h * 0.25f,
                                w * 0.85f, h * 0.35f,
                                w, h * 0.70f
                            )
                        }
                        drawPath(
                            path = progesteronePath,
                            color = progesteroneColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Symptom Highlight Dot (Red) on Progesterone line at current day X
                        val redSymptomColor = Color(0xFFD32F2F)
                        val symptomPoint = Offset(currentDayX, h * 0.33f)

                        // Red outer aura
                        drawCircle(
                            color = redSymptomColor.copy(alpha = 0.2f),
                            radius = 12.dp.toPx(),
                            center = symptomPoint
                        )
                        // Red inner dot
                        drawCircle(
                            color = redSymptomColor,
                            radius = 5.dp.toPx(),
                            center = symptomPoint
                        )
                    }

                    // Phase X-axis Labels at bottom of chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Menstrual", fontFamily = ManropeFontFamily, fontSize = 11.sp, color = Color(0xFF7A6A73))
                        Text("Follicular", fontFamily = ManropeFontFamily, fontSize = 11.sp, color = Color(0xFF7A6A73))
                        Text("Luteal", fontFamily = ManropeFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Legend Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendDot(color = Color(0xFF704257), label = "Estrogen")
                    LegendDot(color = Color(0xFF5B3950), label = "Progesterone")
                    LegendDot(color = Color(0xFFD32F2F), label = "Headaches")
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontFamily = ManropeFontFamily,
            fontSize = 12.sp,
            color = Color(0xFF5E4E57)
        )
    }
}
