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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

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
                text = "Period",
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = Color(0xFF4A3B43)
            )
        }

        // Fertile Dot (Pink Outline)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .border(1.5.dp, Color(0xFFF7CADA), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Fertile",
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = Color(0xFF4A3B43)
            )
        }

        // Ovulation Indicator (Pink Circle + Purple Border Ring)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDE8EF))
                    .border(1.5.dp, Primary, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Ovulation",
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = Color(0xFF4A3B43)
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
    modifier: Modifier = Modifier
) {
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
                    .background(Color(0xFFFDF0F4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Spa,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$phaseName  •  Day $dayNumber",
                    fontFamily = LiterataFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF26181F)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = description,
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFF594852)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF4EDF0))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Next period in $daysUntilNextPeriod days",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF52424A)
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
    text: String = "Your cycle length is steady at 28 days.",
    onViewAllClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF4F7)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEDEE5)),
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
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                color = Color(0xFF3B2C34),
                modifier = Modifier.weight(1f),
                lineHeight = 19.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "View\nAll",
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }
    }
}
