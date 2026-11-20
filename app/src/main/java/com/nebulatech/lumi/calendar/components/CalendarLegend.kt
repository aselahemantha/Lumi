package com.nebulatech.lumi.calendar.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.nebulatech.lumi.ui.theme.DestructiveRed
import com.nebulatech.lumi.ui.theme.RoseTintLight
import com.nebulatech.lumi.ui.theme.RoseTintLightest
import com.nebulatech.lumi.ui.theme.RoseTintMedium
import com.nebulatech.lumi.ui.theme.SurfaceContainerLow
import com.nebulatech.lumi.ui.theme.TextPrimary
import com.nebulatech.lumi.ui.theme.TextSecondary
import com.nebulatech.lumi.ui.theme.TextStrong

// ==========================================
// 1. CALENDAR LEGEND
// ==========================================
@Composable
fun CalendarLegend(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Period Dot (Solid Plum)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Primary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.legend_period),
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = TextStrong
            )
        }

        // Fertile Dot (Pink Outline)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .border(1.5.dp, RoseTintMedium, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.legend_fertile),
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = TextStrong
            )
        }

        // Ovulation Indicator (Pink Circle + Purple Border Ring)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(RoseTintLight)
                    .border(1.5.dp, Primary, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.legend_ovulation),
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = TextStrong
            )
        }
    }
}

// ==========================================
// 2. PHASE DETAIL CARD
// ==========================================
@Composable
fun PhaseDetailCard(
    phaseName: String = "Follicular Phase",
    dayNumber: Int = 8,
    description: String = "Estrogen levels are rising. You might feel an increase in energy and focus today.",
    daysUntilNextPeriod: Int = 21,
    statusBadgeText: String? = null,
    isOverdue: Boolean = false,
    modifier: Modifier = Modifier
) {
    val badge = statusBadgeText ?: when {
        isOverdue -> "Period overdue"
        daysUntilNextPeriod > 1 -> "Next period in $daysUntilNextPeriod days"
        daysUntilNextPeriod == 1 -> "Next period in 1 day"
        daysUntilNextPeriod == 0 -> "Period expected today"
        else -> "Period overdue by ${-daysUntilNextPeriod} days"
    }

    val badgeBg = if (isOverdue) RoseTintLight else SurfaceContainerLow
    val badgeTextColor = if (isOverdue) DestructiveRed else TextSecondary
    val iconBg = if (isOverdue) RoseTintLight else RoseTintLightest
    val iconTint = if (isOverdue) DestructiveRed else Primary

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Spa,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.phase_day_format, phaseName, dayNumber),
                    fontFamily = LiterataFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = description,
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = badge,
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. CYCLE STATUS BANNER CARD
// ==========================================
@Composable
fun CycleStatusBannerCard(
    text: String = stringResource(R.string.calendar_cycle_steady_default),
    onViewAllClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RoseTintLightest),
        border = androidx.compose.foundation.BorderStroke(1.dp, RoseTintLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                color = TextStrong,
                modifier = Modifier.weight(1f),
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.view_all),
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onViewAllClick() }
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            )
        }
    }
}
