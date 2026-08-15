package com.nebulatech.lumi.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.LumiTheme
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

@Composable
fun NotificationCenterScreen(
    onNavigateBack: () -> Unit = {},
    onTabSelected: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sampleNotifications = remember {
        listOf(
            LumiNotificationItem(
                id = "1",
                category = NotificationCategory.DAILY_REFLECTION,
                title = "Daily Reflection",
                body = "How are you feeling today? Take 10 seconds to log today's symptoms and mood.",
                timeText = "8:30 PM"
            ),
            LumiNotificationItem(
                id = "2",
                category = NotificationCategory.MORNING_BBT,
                title = "Good Morning",
                body = "Remember to take your waking temperature before getting up.",
                timeText = "7:00 AM"
            ),
            LumiNotificationItem(
                id = "3",
                category = NotificationCategory.PERIOD_PREDICTION,
                title = "Period Prediction",
                body = "Your period is predicted in ~2 days. Stay hydrated and prioritize restorative rest.",
                timeText = "9:00 AM",
                badgeIcon = Icons.Outlined.WaterDrop,
                badgeBgColor = Color(0xFFFDE8EF),
                badgeIconColor = Color(0xFF8E5572)
            ),
            LumiNotificationItem(
                id = "4",
                category = NotificationCategory.FERTILITY_INSIGHT,
                title = "Fertility Insight",
                body = "Your fertile window begins tomorrow. Chances of conception are rising.",
                timeText = "9:00 AM",
                badgeIcon = Icons.Outlined.Spa,
                badgeBgColor = Color(0xFFFDE8F4),
                badgeIconColor = Color(0xFFB54876)
            ),
            LumiNotificationItem(
                id = "5",
                category = NotificationCategory.PEAK_VITALITY,
                title = "Peak Vitality",
                body = "Peak fertility window today! Today is an optimal time for an LH ovulation test.",
                timeText = "11:00 AM",
                badgeIcon = Icons.Outlined.AutoAwesome,
                badgeBgColor = Color(0xFFFDF0E4),
                badgeIconColor = Color(0xFFD97706)
            ),
            LumiNotificationItem(
                id = "6",
                category = NotificationCategory.PHASE_INSIGHT,
                title = "Luteal Phase",
                body = "You've entered the Luteal phase. Consider swapping high-intensity workouts for yoga to balance cortisol.",
                timeText = "10:00 AM",
                badgeIcon = Icons.Outlined.Grain,
                badgeBgColor = Color(0xFFEFE8EB),
                badgeIconColor = Color(0xFF5B3950)
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            LumiBottomNavigationBar(
                selectedTab = HomeTab.PROFILE,
                onTabSelected = onTabSelected
            )
        },
        containerColor = Color(0xFFFBF9F7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Title & Subtitle (Left-aligned, consistent with Profile and Insights)
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = "Notification Center",
                    fontFamily = LiterataFontFamily,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Preview of system alerts, reminders, and phase insights designed to gently support your daily wellness journey.",
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFF5E4E57)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Notification Cards List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                sampleNotifications.forEach { notification ->
                    NotificationItemCard(item = notification)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCenterScreenPreview() {
    LumiTheme {
        NotificationCenterScreen()
    }
}
