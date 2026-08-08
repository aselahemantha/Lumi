package com.nebulatech.lumi.home.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nebulatech.lumi.home.components.BasalBodyTempChartCard
import com.nebulatech.lumi.home.components.FertilityHeaderCard
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.HomeTopBar
import com.nebulatech.lumi.home.components.LibraryFeaturedCard
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.home.components.LumiInsightCard
import com.nebulatech.lumi.home.components.TodaysLogsCard
import com.nebulatech.lumi.ui.theme.LumiTheme

/**
 * Layout 3: High Fertility Dashboard Screen
 * Triggered during FERTILE_WINDOW phase (Days 11–15 of a 28-day cycle).
 * Focuses on peak ovulation status, BBT/LH logging, temperature trends, and ovulation insights.
 */
@Composable
fun FertilityDashboardHomeScreen(
    onLogBBTClick: () -> Unit = {},
    onLogLHClick: () -> Unit = {},
    onAddMoreLogsClick: () -> Unit = {},
    onReadArticleClick: () -> Unit = {},
    onTabSelected: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(showNotificationBell = false)
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
            // 1. Fertility Header Card
            FertilityHeaderCard(
                statusTag = "CURRENT STATUS",
                title = "High Fertility Today",
                description = "Ovulation expected tomorrow. This is your peak window.",
                onLogBBTClick = onLogBBTClick,
                onLogLHClick = onLogLHClick
            )

            // 2. Daily Insight Card
            LumiInsightCard(
                title = "Daily Insight",
                text = "Your BBT spiked by 0.6° today and you logged egg-white cervical mucus. Likelihood of ovulation is peaking. If you are trying to conceive, today and tomorrow are optimal."
            )

            // 3. Basal Body Temp Chart
            BasalBodyTempChartCard()

            // 4. Today's Logs Card
            TodaysLogsCard(
                onAddMoreLogsClick = onAddMoreLogsClick
            )

            // 5. Contextual Library Card
            LibraryFeaturedCard(
                title = "Understanding LH Surges",
                onReadArticleClick = onReadArticleClick
            )

            Spacer(modifier = Modifier.height(16.dp))
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
