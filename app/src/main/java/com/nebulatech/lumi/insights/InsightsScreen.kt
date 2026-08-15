package com.nebulatech.lumi.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.insights.components.CycleAtAGlanceSection
import com.nebulatech.lumi.insights.components.HormonePhaseStatusCard
import com.nebulatech.lumi.insights.components.InsightsLumiBannerCard
import com.nebulatech.lumi.insights.components.LearnArticlesSection
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.LumiTheme
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary
import org.koin.androidx.compose.koinViewModel

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    onTabSelected: (HomeTab) -> Unit = {}
) {
    val viewModel: InsightsViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            LumiBottomNavigationBar(
                selectedTab = HomeTab.INSIGHTS,
                onTabSelected = onTabSelected
            )
        },
        containerColor = Color(0xFFFBF9F7)
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
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
            // Header Title & Subtitle
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = "Insights",
                    fontFamily = LiterataFontFamily,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Understanding your unique rhythms.",
                    fontFamily = ManropeFontFamily,
                    fontSize = 15.sp,
                    color = Color(0xFF5E4E57)
                )
            }

            // 1. Hormone & Phase Status Widget (Clean non-chart status card, moved up!)
            HormonePhaseStatusCard(
                currentPhase = state.currentPhase,
                currentCycleDay = state.currentCycleDay,
                cycleLength = state.cycleLength,
                loggedSymptoms = state.loggedSymptomPoints
            )

            // 2. Dynamic Lumi AI Insight Dark Banner
            InsightsLumiBannerCard(
                text = state.dynamicInsightText.ifBlank {
                    "We noticed your high-energy days consistently align with your Follicular phase. This is a great time for creative projects or intense workouts."
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
