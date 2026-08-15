package com.nebulatech.lumi.notifications

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class NotificationCategory {
    DAILY_REFLECTION,
    MORNING_BBT,
    PERIOD_PREDICTION,
    FERTILITY_INSIGHT,
    PEAK_VITALITY,
    PHASE_INSIGHT
}

data class LumiNotificationItem(
    val id: String,
    val category: NotificationCategory,
    val title: String,
    val body: String,
    val timeText: String,
    val badgeIcon: ImageVector? = null,
    val badgeBgColor: Color = Color(0xFFFDE8EF),
    val badgeIconColor: Color = Color(0xFF8E5572)
)
