package com.nebulatech.lumi.notifications

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

@Composable
fun NotificationItemCard(
    item: LumiNotificationItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // App Icon Box with category badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(2.dp)
            ) {
                // Main Lumi App Icon Tile
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF7EFE9)),
                    contentAlignment = Alignment.Center
                ) {
                    // Stylized Lumi "L" organic glyph
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.32f, size.height * 0.28f)
                            cubicTo(
                                size.width * 0.32f, size.height * 0.65f,
                                size.width * 0.35f, size.height * 0.72f,
                                size.width * 0.58f, size.height * 0.72f
                            )
                            cubicTo(
                                size.width * 0.75f, size.height * 0.72f,
                                size.width * 0.82f, size.height * 0.62f,
                                size.width * 0.82f, size.height * 0.62f
                            )
                        }
                        drawPath(
                            path = path,
                            color = Primary,
                            style = Stroke(
                                width = 3.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }

                // Optional Category Overlay Badge (Bottom Right)
                if (item.badgeIcon != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(1.dp)
                            .clip(CircleShape)
                            .background(item.badgeBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.badgeIcon,
                            contentDescription = null,
                            tint = item.badgeIconColor,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Notification Text Content
            Column(modifier = Modifier.weight(1f)) {
                // Header Row: App Name ("Lumi") and Timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lumi",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6E5E67)
                    )

                    Text(
                        text = item.timeText,
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF8A7A83)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Notification Title
                Text(
                    text = item.title,
                    fontFamily = LiterataFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF26181F)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Notification Body
                Text(
                    text = item.body,
                    fontFamily = ManropeFontFamily,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = Color(0xFF5E4E57)
                )
            }
        }
    }
}
