package com.nebulatech.lumi.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nebulatech.lumi.R
import com.nebulatech.lumi.analytics.AnalyticsConstants
import com.nebulatech.lumi.analytics.LocalAnalyticsTracker
import com.nebulatech.lumi.analytics.TrackScreenView
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.insights.components.CycleAtAGlanceSection
import com.nebulatech.lumi.insights.components.HormonePhaseStatusCard
import com.nebulatech.lumi.insights.components.InsightsLumiBannerCard
import com.nebulatech.lumi.insights.components.LearnArticlesSection
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.LumiTheme
import com.nebulatech.lumi.ui.theme.Primary
import org.koin.androidx.compose.koinViewModel
import com.nebulatech.lumi.ui.theme.Background
import com.nebulatech.lumi.ui.theme.TextStrong

@Composable
fun InsightsTopBar(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.insights_title),
            fontFamily = LiterataFontFamily,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )

        IconButton(onClick = onNotificationClick) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.notifications_desc),
                tint = TextStrong,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    onTabSelected: (HomeTab) -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    val viewModel: InsightsViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tracker = LocalAnalyticsTracker.current

    TrackScreenView(AnalyticsConstants.Screens.INSIGHTS)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { InsightsTopBar(onNotificationClick = onNotificationClick) },
        bottomBar = {
            LumiBottomNavigationBar(
                selectedTab = HomeTab.INSIGHTS,
                onTabSelected = onTabSelected
            )
        },
        containerColor = Background
    ) { innerPadding ->
        if (state.isLoading) {
            com.nebulatech.lumi.insights.components.InsightsScreenSkeleton(
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Hormone & Phase Status Widget
                HormonePhaseStatusCard(
                    currentPhase = state.currentPhase,
                    currentCycleDay = state.currentCycleDay,
                    cycleLength = state.cycleLength,
                    loggedSymptoms = state.loggedSymptomPoints
                )

                // 2. Dynamic Lumi AI Insight Dark Banner
                InsightsLumiBannerCard(
                    text = state.dynamicInsightText.ifBlank {
                        "Estrogen and progesterone shift throughout your cycle. Log daily symptoms to receive personalized phase insights."
                    }
                )

                // 3. Cycle At A Glance (Room cycles history)
                CycleAtAGlanceSection(
                    cycles = state.cycleHistory
                )

                // 4. Learn Section (Online Cloud Sync banner)
                LearnArticlesSection()

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InsightsScreenPreview() {
    LumiTheme {
        InsightsScreen()
    }
}
