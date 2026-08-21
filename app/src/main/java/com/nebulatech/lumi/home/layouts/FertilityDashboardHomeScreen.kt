package com.nebulatech.lumi.home.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nebulatech.lumi.analytics.AnalyticsConstants
import com.nebulatech.lumi.analytics.LocalAnalyticsTracker
import com.nebulatech.lumi.home.components.BasalBodyTempChartCard
import com.nebulatech.lumi.home.components.FertilityHeaderCard
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.HomeTopBar
import com.nebulatech.lumi.home.components.LibraryFeaturedCard
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.home.components.LumiInsightCard
import com.nebulatech.lumi.home.components.TodaysLogsCard
import com.nebulatech.lumi.logging.LogBBTBottomSheet
import com.nebulatech.lumi.logging.LogFlowBottomSheet
import com.nebulatech.lumi.logging.LogLHTestBottomSheet
import com.nebulatech.lumi.logging.LoggingAction
import com.nebulatech.lumi.logging.LoggingEvent
import com.nebulatech.lumi.logging.LoggingViewModel
import com.nebulatech.lumi.ui.theme.LumiTheme

/**
 * Layout 3: High Fertility Dashboard Screen
 * Triggered during FERTILE_WINDOW phase.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FertilityDashboardHomeScreen(
    modifier: Modifier = Modifier,
    userName: String = "",
    loggingViewModel: LoggingViewModel? = null,
    onLogBBTClick: () -> Unit = {},
    onLogLHClick: () -> Unit = {},
    onAddMoreLogsClick: () -> Unit = {},
    onReadArticleClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onTabSelected: (HomeTab) -> Unit = {}
) {
    var showBBTSheet by remember { mutableStateOf(false) }
    var showLHSheet by remember { mutableStateOf(false) }
    var showFlowSheet by remember { mutableStateOf(false) }
    val tracker = LocalAnalyticsTracker.current

    LaunchedEffect(loggingViewModel) {
        loggingViewModel?.events?.collect { event ->
            when (event) {
                is LoggingEvent.BbtSaved -> showBBTSheet = false
                is LoggingEvent.LhSaved -> showLHSheet = false
                is LoggingEvent.FlowSaved -> showFlowSheet = false
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FertilityHeaderCard(
                statusTag = "CURRENT STATUS",
                title = "High Fertility Today",
                description = "Ovulation expected tomorrow. This is your peak window.",
                onLogBBTClick = {
                    tracker.trackButtonClick("btn_log_bbt", AnalyticsConstants.Screens.HOME_FERTILITY_DASHBOARD)
                    showBBTSheet = true
                    onLogBBTClick()
                },
                onLogLHClick = {
                    tracker.trackButtonClick("btn_log_lh", AnalyticsConstants.Screens.HOME_FERTILITY_DASHBOARD)
                    showLHSheet = true
                    onLogLHClick()
                }
            )

            LumiInsightCard(
                title = "Daily Insight",
                text = "Your BBT spiked by 0.6° today and you logged egg-white cervical mucus. Likelihood of ovulation is peaking. If you are trying to conceive, today and tomorrow are optimal."
            )

            BasalBodyTempChartCard()

            TodaysLogsCard(
                onAddMoreLogsClick = {
                    tracker.trackButtonClick(AnalyticsConstants.Buttons.QUICK_LOG_TODAY, AnalyticsConstants.Screens.HOME_FERTILITY_DASHBOARD)
                    showFlowSheet = true
                    onAddMoreLogsClick()
                }
            )

            LibraryFeaturedCard(
                title = "Understanding LH Surges",
                onReadArticleClick = {
                    tracker.trackButtonClick("btn_read_article_lh", AnalyticsConstants.Screens.HOME_FERTILITY_DASHBOARD)
                    onReadArticleClick()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showBBTSheet) {
            LogBBTBottomSheet(
                onDismissRequest = { showBBTSheet = false },
                onSaveReading = { tempStr, disturbedSleep, feverIllness ->
                    loggingViewModel?.onAction(
                        LoggingAction.SaveBbt(
                            temperatureStr = tempStr,
                            disturbedSleep = disturbedSleep,
                            feverIllness = feverIllness
                        )
                    ) ?: run { showBBTSheet = false }
                }
            )
        }

        if (showLHSheet) {
            LogLHTestBottomSheet(
                onDismissRequest = { showLHSheet = false },
                onSaveResult = { intensity, brand, _ ->
                    loggingViewModel?.onAction(
                        LoggingAction.SaveLhTest(
                            intensity = intensity,
                            brand = brand.takeIf { it.isNotBlank() }
                        )
                    ) ?: run { showLHSheet = false }
                }
            )
        }

        if (showFlowSheet) {
            LogFlowBottomSheet(
                onDismissRequest = { showFlowSheet = false },
                onSaveLog = { flow, symptoms, mood ->
                    loggingViewModel?.onAction(
                        LoggingAction.SaveFlowLog(
                            flow = flow,
                            mood = mood,
                            symptoms = symptoms
                        )
                    ) ?: run { showFlowSheet = false }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FertilityDashboardHomeScreenPreview() {
    LumiTheme {
        FertilityDashboardHomeScreen()
    }
}
