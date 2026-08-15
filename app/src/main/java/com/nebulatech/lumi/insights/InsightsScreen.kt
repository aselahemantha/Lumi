package com.nebulatech.lumi.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.home.components.StandardLumiTopBar
import com.nebulatech.lumi.insights.components.CycleAtAGlanceSection
import com.nebulatech.lumi.insights.components.HormoneSymptomTrendsCard
import com.nebulatech.lumi.insights.components.InsightsLumiBannerCard
import com.nebulatech.lumi.insights.components.InteractiveHormoneChartCard
import com.nebulatech.lumi.insights.components.LearnArticlesSection
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.LumiTheme
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

@Composable
fun InsightsTopBar(
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    StandardLumiTopBar(
        onProfileClick = onProfileClick,
        modifier = modifier
    )
}

@Composable
fun InsightsScreen(
    onTabSelected: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            InsightsTopBar(
                onProfileClick = { onTabSelected(HomeTab.PROFILE) }
            )
        },
        bottomBar = {
            LumiBottomNavigationBar(
                selectedTab = HomeTab.INSIGHTS,
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Title & Subtitle
            Column {
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

            // 1. Cycle At A Glance
            CycleAtAGlanceSection()

            // 2. Interactive Hormone Cycle Tracker Chart
            InteractiveHormoneChartCard()

            // 3. Hormone & Symptom Trends Summary
            HormoneSymptomTrendsCard()

            // 4. Lumi Insight Dark Banner
            InsightsLumiBannerCard()

            // 5. Learn Section
            LearnArticlesSection()

            Spacer(modifier = Modifier.height(16.dp))
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
