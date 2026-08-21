package com.nebulatech.lumi.home.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.analytics.AnalyticsConstants
import com.nebulatech.lumi.analytics.LocalAnalyticsTracker
import com.nebulatech.lumi.home.components.CycleRingWidget
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.HomeTopBar
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.home.components.LumiInsightCard
import com.nebulatech.lumi.home.components.Next7DaysCalendarStrip
import com.nebulatech.lumi.logging.LogFlowBottomSheet
import com.nebulatech.lumi.logging.LoggingAction
import com.nebulatech.lumi.logging.LoggingEvent
import com.nebulatech.lumi.logging.LoggingViewModel
import com.nebulatech.lumi.ui.theme.LumiTheme
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

/**
 * Layout 2: Standard Cycle Ring Screen
 * Triggered during MENSTRUATION, FOLLICULAR, and LUTEAL phases.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CycleRingHomeScreen(
    modifier: Modifier = Modifier,
    cycleDay: Int = 1,
    cycleDayTotal: Int = 28,
    progressRatio: Float = 0f,
    subLabelText: String = "",
    userName: String = "",
    insightTitle: String = "Lumi Insight",
    insightText: String = "",
    isPeriodPredicted: Boolean = false,
    next7Days: List<com.nebulatech.lumi.home.components.DayItem> = emptyList(),
    loggingViewModel: LoggingViewModel? = null,
    onLogFlowClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onTabSelected: (HomeTab) -> Unit = {}
) {
    var showLogSheet by remember { mutableStateOf(false) }
    val tracker = LocalAnalyticsTracker.current

    // Dismiss sheet when save succeeds
    LaunchedEffect(loggingViewModel) {
        loggingViewModel?.events?.collect { event ->
            when (event) {
                is LoggingEvent.FlowSaved -> showLogSheet = false
                else -> Unit
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(
                userName = userName,
                showNotificationBell = true,
                onNotificationClick = onNotificationClick,
                onProfileClick = { onTabSelected(HomeTab.PROFILE) }
            )
        },
        bottomBar = {
            LumiBottomNavigationBar(
                selectedTab = HomeTab.TODAY,
                onTabSelected = onTabSelected
            )
        },
        containerColor = Color(0xFFFBF9F7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Cycle Ring Arc
            CycleRingWidget(
                cycleDay = cycleDay,
                subLabelText = subLabelText.ifBlank { "Day $cycleDay of $cycleDayTotal" },
                progressRatio = progressRatio
            )

            // 2. Lumi Insight Card (Shown right below Cycle Ring)
            LumiInsightCard(
                title = insightTitle.ifBlank { "Lumi Insight" },
                text = insightText.ifBlank {
                    "Your last 3 cycles have varied by 8 days. To help stabilize ovulation this week, try swapping high-intensity workouts for yoga."
                }
            )

            // 3. Primary Log Flow / Confirm Period Button
            Button(
                onClick = {
                    tracker.trackButtonClick(
                        buttonName = AnalyticsConstants.Buttons.LOG_PERIOD_FLOW,
                        screenName = AnalyticsConstants.Screens.HOME_CYCLE_RING
                    )
                    showLogSheet = true
                    onLogFlowClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPeriodPredicted) Color(0xFF7B3F5E) else Primary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPeriodPredicted) "Confirm Period Start" else "Log Flow",
                        fontFamily = ManropeFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 4. Next 7 Days Calendar Strip
            if (next7Days.isNotEmpty()) {
                Next7DaysCalendarStrip(days = next7Days)
            } else {
                Next7DaysCalendarStrip()
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showLogSheet) {
            LogFlowBottomSheet(
                onDismissRequest = { showLogSheet = false },
                onSaveLog = { flow, symptoms, mood ->
                    loggingViewModel?.onAction(
                        LoggingAction.SaveFlowLog(
                            flow = flow,
                            mood = mood,
                            symptoms = symptoms
                        )
                    ) ?: run { showLogSheet = false }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CycleRingHomeScreenPreview() {
    LumiTheme {
        CycleRingHomeScreen(
            cycleDay = 12,
            cycleDayTotal = 28,
            progressRatio = 0.43f,
            subLabelText = "Fertile window in ~2 days"
        )
    }
}
