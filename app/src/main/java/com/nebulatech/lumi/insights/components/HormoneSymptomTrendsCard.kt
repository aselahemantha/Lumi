package com.nebulatech.lumi.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.insights.SymptomTrendPoint
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HormonePhaseStatusCard(
    modifier: Modifier = Modifier,
    currentPhase: CyclePhase = CyclePhase.FOLLICULAR,
    currentCycleDay: Int = 1,
    cycleLength: Int = 28,
    loggedSymptoms: List<SymptomTrendPoint> = emptyList()
) {
    val isOverdue = (currentPhase == CyclePhase.PERIOD_PREDICTED || currentPhase == CyclePhase.LATE_LUTEAL) && currentCycleDay > cycleLength
    val overdueDays = if (isOverdue) currentCycleDay - cycleLength else 0

    val phaseName = when {
        isOverdue -> "Period Overdue"
        currentPhase == CyclePhase.MENSTRUATION -> "Menstrual Phase"
        currentPhase == CyclePhase.FOLLICULAR -> "Follicular Phase"
        currentPhase == CyclePhase.FERTILE_WINDOW -> "Fertile Window"
        currentPhase == CyclePhase.LUTEAL -> "Luteal Phase"
        currentPhase == CyclePhase.LATE_LUTEAL -> "Late Luteal Phase"
        currentPhase == CyclePhase.PERIOD_PREDICTED -> "Period Expected"
        else -> "Period Expected"
    }

    val subtitleText = if (isOverdue) {
        "Day $currentCycleDay ($overdueDays ${if (overdueDays == 1) "day" else "days"} past expected)"
    } else {
        "Day $currentCycleDay of $cycleLength"
    }

    val dominantHormone = when {
        isOverdue -> "Luteal Variation"
        currentPhase == CyclePhase.MENSTRUATION -> "Baseline Levels"
        currentPhase == CyclePhase.FOLLICULAR -> "Estrogen Rising"
        currentPhase == CyclePhase.FERTILE_WINDOW -> "LH Surge"
        currentPhase == CyclePhase.LUTEAL -> "Progesterone Dominant"
        currentPhase == CyclePhase.LATE_LUTEAL -> "Hormone Taper"
        currentPhase == CyclePhase.PERIOD_PREDICTED -> "Hormone Taper"
        else -> "Hormone Taper"
    }

    val estrogenLevel = when (currentPhase) {
        CyclePhase.MENSTRUATION -> "Low"
        CyclePhase.FOLLICULAR -> "High ↑"
        CyclePhase.FERTILE_WINDOW -> "Peak ↑↑"
        CyclePhase.LUTEAL -> "Moderate"
        CyclePhase.LATE_LUTEAL -> "Declining ↓"
        CyclePhase.PERIOD_PREDICTED -> "Low ↓"
    }

    val progesteroneLevel = when (currentPhase) {
        CyclePhase.MENSTRUATION -> "Low"
        CyclePhase.FOLLICULAR -> "Baseline"
        CyclePhase.FERTILE_WINDOW -> "Rising ↑"
        CyclePhase.LUTEAL -> "Peak ↑↑"
        CyclePhase.LATE_LUTEAL -> "Declining ↓"
        CyclePhase.PERIOD_PREDICTED -> "Low ↓"
    }

    val energyFocus = when (currentPhase) {
        CyclePhase.MENSTRUATION -> "Restorative"
        CyclePhase.FOLLICULAR -> "Peak Focus"
        CyclePhase.FERTILE_WINDOW -> "High Vitality"
        CyclePhase.LUTEAL -> "Grounded"
        CyclePhase.LATE_LUTEAL -> "Reflective"
        CyclePhase.PERIOD_PREDICTED -> "Transitioning"
    }

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
            // Header Row: Phase Name + Dominant Hormone Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isOverdue) Color(0xFFFBECEE) else Color(0xFFFDF0F4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Spa,
                            contentDescription = null,
                            tint = if (isOverdue) Color(0xFFB3261E) else Primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = phaseName,
                            fontFamily = LiterataFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF26181F)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitleText,
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            color = if (isOverdue) Color(0xFFB3261E) else Color(0xFF7A6A73),
                            fontWeight = if (isOverdue) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isOverdue) Color(0xFFFCE8EC) else Color(0xFFF4E9EE))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = dominantHormone,
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverdue) Color(0xFFB3261E) else Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color(0xFFF2ECEF), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Hormone Levels Row (3 status blocks)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HormoneStatBlock(
                    label = "Estrogen",
                    value = estrogenLevel,
                    accentColor = Color(0xFF8E5572),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                HormoneStatBlock(
                    label = "Progesterone",
                    value = progesteroneLevel,
                    accentColor = Color(0xFF5B3047),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                HormoneStatBlock(
                    label = "Energy State",
                    value = energyFocus,
                    accentColor = Primary,
                    modifier = Modifier.weight(1.1f)
                )
            }

            // Logged Symptoms Chips (if any)
            if (loggedSymptoms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "RECENT SYMPTOMS LOGGED",
                    fontFamily = ManropeFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color(0xFF8A7A83)
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    loggedSymptoms.take(6).forEach { symptom ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFBF4F7))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${symptom.symptomName} (Day ${symptom.cycleDay})",
                                fontFamily = ManropeFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HormoneStatBlock(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFAF7F8))
            .padding(10.dp)
    ) {
        Text(
            text = label,
            fontFamily = ManropeFontFamily,
            fontSize = 11.sp,
            color = Color(0xFF7A6A73)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontFamily = ManropeFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            maxLines = 1
        )
    }
}
