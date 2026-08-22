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
import com.nebulatech.lumi.analytics.AnalyticsConstants
import com.nebulatech.lumi.analytics.LocalAnalyticsTracker
import com.nebulatech.lumi.analytics.TrackScreenView
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
    val tracker = LocalAnalyticsTracker.current
    val state by homeVm.state.collectAsStateWithLifecycle()

    val selectedBottomTab = remember { mutableStateOf(HomeTab.TODAY) }
    val showNotificationCenter = remember { mutableStateOf(false) }

    // Derive active screen name for screen view tracking
    val activeScreenName = remember(selectedBottomTab.value, showNotificationCenter.value, state.layoutType) {
        if (showNotificationCenter.value) {
            AnalyticsConstants.Screens.NOTIFICATION_CENTER
        } else {
            when (selectedBottomTab.value) {
                HomeTab.TODAY -> when (state.layoutType) {
                    HomeLayoutType.CYCLE_RING -> AnalyticsConstants.Screens.HOME_CYCLE_RING
                    HomeLayoutType.FERTILITY_DASHBOARD -> AnalyticsConstants.Screens.HOME_FERTILITY_DASHBOARD
                    HomeLayoutType.SYMPTOM_GRID -> AnalyticsConstants.Screens.HOME_LATE_LUTEAL
                }
                HomeTab.CALENDAR -> AnalyticsConstants.Screens.CALENDAR
                HomeTab.INSIGHTS -> AnalyticsConstants.Screens.INSIGHTS
                HomeTab.PROFILE -> AnalyticsConstants.Screens.PROFILE
            }
        }
    }

    TrackScreenView(screenName = activeScreenName)

    val onTabSelectedWithAnalytics: (HomeTab) -> Unit = { tab ->
        val buttonName = when (tab) {
            HomeTab.TODAY -> AnalyticsConstants.Buttons.TAB_TODAY
            HomeTab.CALENDAR -> AnalyticsConstants.Buttons.TAB_CALENDAR
            HomeTab.INSIGHTS -> AnalyticsConstants.Buttons.TAB_INSIGHTS
            HomeTab.PROFILE -> AnalyticsConstants.Buttons.TAB_PROFILE
        }
        tracker.trackButtonClick(
            buttonName = buttonName,
            screenName = activeScreenName,
            extraParams = mapOf(AnalyticsConstants.Params.TAB_NAME to tab.name)
        )
        tracker.trackEvent(
            AnalyticsConstants.Events.TAB_SWITCH,
            mapOf(
                AnalyticsConstants.Params.PREVIOUS_SCREEN to activeScreenName,
                AnalyticsConstants.Params.TAB_NAME to tab.name
            )
        )
        selectedBottomTab.value = tab
    }

    val onNotificationClickWithAnalytics: () -> Unit = {
        tracker.trackButtonClick(
            buttonName = AnalyticsConstants.Buttons.NOTIFICATION_BELL,
            screenName = activeScreenName
        )
        showNotificationCenter.value = true
    }

    // Handle VM events (e.g. navigate to onboarding if no user)
    LaunchedEffect(homeVm.events) {
        homeVm.events.collect { /* future: handle NavigateToOnboarding */ }
    }

    if (showNotificationCenter.value) {
        NotificationCenterScreen(
            onNavigateBack = {
                tracker.trackButtonClick(
                    buttonName = AnalyticsConstants.Buttons.BACK,
                    screenName = AnalyticsConstants.Screens.NOTIFICATION_CENTER
                )
                showNotificationCenter.value = false
            },
            onTabSelected = { tab ->
                onTabSelectedWithAnalytics(tab)
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
                                onTabSelected = onTabSelectedWithAnalytics
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
                                        onNotificationClick = onNotificationClickWithAnalytics,
                                        onTabSelected = onTabSelectedWithAnalytics
                                    )
                                }
                                HomeLayoutType.FERTILITY_DASHBOARD -> {
                                    FertilityDashboardHomeScreen(
                                        userName = state.userName,
                                        loggingViewModel = loggingVm,
                                        onNotificationClick = onNotificationClickWithAnalytics,
                                        onTabSelected = onTabSelectedWithAnalytics
                                    )
                                }
                                HomeLayoutType.SYMPTOM_GRID -> {
                                    LateLutealHomeScreen(
                                        cycleDay = state.cycleDay,
                                        progressRatio = state.progressRatio,
                                        userName = state.userName,
                                        loggingViewModel = loggingVm,
                                        onNotificationClick = onNotificationClickWithAnalytics,
                                        onTabSelected = onTabSelectedWithAnalytics
                                    )
                                }
                            }
                        }
                    }
                    HomeTab.CALENDAR -> {
                        CalendarScreen(onTabSelected = onTabSelectedWithAnalytics)
                    }
                    HomeTab.INSIGHTS -> {
                        InsightsScreen(onTabSelected = onTabSelectedWithAnalytics)
                    }
                    HomeTab.PROFILE -> {
                        ProfileScreen(
                            onTabSelected = onTabSelectedWithAnalytics,
                            onNotificationClick = onNotificationClickWithAnalytics
                        )
                    }
                }
            }
        }
    }
}
