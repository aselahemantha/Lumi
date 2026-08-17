package com.nebulatech.lumi.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nebulatech.lumi.calendar.CalendarScreen
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.layouts.CycleRingHomeScreen
import com.nebulatech.lumi.home.layouts.FertilityDashboardHomeScreen
import com.nebulatech.lumi.home.layouts.LateLutealHomeScreen
import com.nebulatech.lumi.insights.InsightsScreen
import com.nebulatech.lumi.logging.LoggingViewModel
import com.nebulatech.lumi.notifications.NotificationCenterScreen
import com.nebulatech.lumi.profile.ProfileScreen
import org.koin.androidx.compose.koinViewModel

/**
 * Root composable for the home destination.
 * Layout is driven by [HomeViewModel] which derives [HomeLayoutType] from the
 * active [com.nebulatech.lumi.data.model.CyclePhase] read from Room.
 */
@Composable
fun HomeScreenContainer(
    modifier: Modifier = Modifier
) {
    val homeVm: HomeViewModel = koinViewModel()
    val loggingVm: LoggingViewModel = koinViewModel()
    val state by homeVm.state.collectAsStateWithLifecycle()

    val selectedBottomTab = remember { mutableStateOf(HomeTab.TODAY) }
    val showNotificationCenter = remember { mutableStateOf(false) }

    // Handle VM events (e.g. navigate to onboarding if no user)
    LaunchedEffect(homeVm.events) {
        homeVm.events.collect { /* future: handle NavigateToOnboarding */ }
    }

    if (showNotificationCenter.value) {
        NotificationCenterScreen(
            onNavigateBack = { showNotificationCenter.value = false },
            onTabSelected = { tab ->
                selectedBottomTab.value = tab
                showNotificationCenter.value = false
            }
        )
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                when (selectedBottomTab.value) {
                HomeTab.TODAY -> {
                    if (state.isLoading) {
                        com.nebulatech.lumi.home.components.HomeScreenSkeleton(
                            selectedTab = selectedBottomTab.value,
                            onTabSelected = { selectedBottomTab.value = it }
                        )
                    } else {
                        when (state.layoutType) {
                            HomeLayoutType.CYCLE_RING -> {
                                CycleRingHomeScreen(
                                    cycleDay = state.cycleDay,
                                    cycleDayTotal = state.cycleLength,
                                    progressRatio = state.progressRatio,
                                    subLabelText = state.subLabelText,
                                    userName = state.userName,
                                    insightTitle = state.insightTitle,
                                    insightText = state.insightText,
                                    isPeriodPredicted = state.isPeriodPredicted,
                                    next7Days = state.next7Days,
                                    loggingViewModel = loggingVm,
                                    onNotificationClick = { showNotificationCenter.value = true },
                                    onTabSelected = { selectedBottomTab.value = it }
                                )
                            }
                            HomeLayoutType.FERTILITY_DASHBOARD -> {
                                FertilityDashboardHomeScreen(
                                    userName = state.userName,
                                    loggingViewModel = loggingVm,
                                    onNotificationClick = { showNotificationCenter.value = true },
                                    onTabSelected = { selectedBottomTab.value = it }
                                )
                            }
                            HomeLayoutType.SYMPTOM_GRID -> {
                                LateLutealHomeScreen(
                                    cycleDay = state.cycleDay,
                                    progressRatio = state.progressRatio,
                                    userName = state.userName,
                                    loggingViewModel = loggingVm,
                                    onNotificationClick = { showNotificationCenter.value = true },
                                    onTabSelected = { selectedBottomTab.value = it }
                                )
                            }
                        }
                    }
                }
                HomeTab.CALENDAR -> {
                    CalendarScreen(onTabSelected = { selectedBottomTab.value = it })
                }
                HomeTab.INSIGHTS -> {
                    InsightsScreen(onTabSelected = { selectedBottomTab.value = it })
                }
                HomeTab.PROFILE -> {
                    ProfileScreen(
                        onTabSelected = { selectedBottomTab.value = it },
                        onNotificationClick = { showNotificationCenter.value = true }
                    )
                }
            }
        }
    }
}
}
