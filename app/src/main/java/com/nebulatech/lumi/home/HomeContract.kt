package com.nebulatech.lumi.home

import com.nebulatech.lumi.data.model.CyclePhase

import com.nebulatech.lumi.home.components.DayItem

enum class HomeLayoutType {
    CYCLE_RING,
    FERTILITY_DASHBOARD,
    SYMPTOM_GRID
}

data class HomeState(
    val isLoading: Boolean = true,
    val layoutType: HomeLayoutType = HomeLayoutType.CYCLE_RING,
    val cycleDay: Int = 1,
    val cycleLength: Int = 28,
    val periodLength: Int = 5,
    val progressRatio: Float = 0f,
    val subLabelText: String = "",
    val userName: String = "",
    val hasCycle: Boolean = false,
    val currentPhase: CyclePhase = CyclePhase.FOLLICULAR,
    val insightTitle: String = "Lumi Insight",
    val insightText: String = "",
    val isPeriodPredicted: Boolean = false,
    val next7Days: List<DayItem> = emptyList()
)

sealed interface HomeAction {
    data object StartNewPeriod : HomeAction
    data object ConfirmPeriodStart : HomeAction
}

sealed interface HomeEvent {
    data object NavigateToOnboarding : HomeEvent
}
