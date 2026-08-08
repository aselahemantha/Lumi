package com.nebulatech.lumi.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.calendar.CalendarScreen
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.layouts.CycleRingHomeScreen
import com.nebulatech.lumi.home.layouts.FertilityDashboardHomeScreen
import com.nebulatech.lumi.home.layouts.LateLutealHomeScreen
import com.nebulatech.lumi.insights.InsightsScreen
import com.nebulatech.lumi.profile.ProfileScreen
import com.nebulatech.lumi.ui.theme.LumiTheme
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

enum class HomeLayoutType(val displayName: String) {
    CYCLE_RING("Layout 2: Cycle Ring (Days 1–21)"),
    FERTILITY_DASHBOARD("Layout 3: High Fertility (Days 11–15)"),
    SYMPTOM_GRID("Layout 1: Late Luteal (Days 22–28)")
}

/**
 * Main Home Screen Container.
 * Includes layout selector for previewing the 3 cycle phase views on the Today tab,
 * and allows switching across all 4 bottom tabs: Today, Calendar, Insights, and Profile.
 */
@Composable
fun HomeScreenContainer(
    modifier: Modifier = Modifier,
    initialLayout: HomeLayoutType = HomeLayoutType.CYCLE_RING
) {
    var currentLayout by remember { mutableStateOf(initialLayout) }
    var selectedBottomTab by remember { mutableStateOf(HomeTab.TODAY) }

    Column(modifier = modifier.fillMaxSize()) {
        if (selectedBottomTab == HomeTab.TODAY) {
            // Layout Selector Bar (For previewing & testing the 3 home views)
            ScrollableTabRow(
                selectedTabIndex = currentLayout.ordinal,
                edgePadding = 12.dp,
                containerColor = Primary.copy(alpha = 0.08f),
                contentColor = Primary
            ) {
                HomeLayoutType.entries.forEach { layout ->
                    Tab(
                        selected = currentLayout == layout,
                        onClick = { currentLayout = layout },
                        text = {
                            Text(
                                text = layout.displayName,
                                fontFamily = ManropeFontFamily,
                                fontSize = 12.sp,
                                fontWeight = if (currentLayout == layout) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedBottomTab) {
                HomeTab.TODAY -> {
                    when (currentLayout) {
                        HomeLayoutType.CYCLE_RING -> {
                            CycleRingHomeScreen(
                                onTabSelected = { selectedBottomTab = it }
                            )
                        }
                        HomeLayoutType.FERTILITY_DASHBOARD -> {
                            FertilityDashboardHomeScreen(
                                onTabSelected = { selectedBottomTab = it }
                            )
                        }
                        HomeLayoutType.SYMPTOM_GRID -> {
                            LateLutealHomeScreen(
                                onTabSelected = { selectedBottomTab = it }
                            )
                        }
                    }
                }
                HomeTab.CALENDAR -> {
                    CalendarScreen(
                        onTabSelected = { selectedBottomTab = it }
                    )
                }
                HomeTab.INSIGHTS -> {
                    InsightsScreen(
                        onTabSelected = { selectedBottomTab = it }
                    )
                }
                HomeTab.PROFILE -> {
                    ProfileScreen(
                        onTabSelected = { selectedBottomTab = it }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenContainerPreview() {
    LumiTheme {
        HomeScreenContainer()
    }
}
