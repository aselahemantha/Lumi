package com.nebulatech.lumi.onboarding

import kotlinx.serialization.Serializable

@Serializable
object OnboardingRoute

enum class OnboardingGoal {
    TRACK_CYCLE,
    UNDERSTAND_SYMPTOMS,
    OPTIMIZE_FERTILITY
}

data class OnboardingState(
    val selectedGoal: OnboardingGoal? = null
)

sealed interface OnboardingAction {
    data class SelectGoal(val goal: OnboardingGoal) : OnboardingAction
    data object ClickBack : OnboardingAction
    data object ClickContinue : OnboardingAction
}

sealed interface OnboardingEvent {
    data object NavigateBack : OnboardingEvent
    data class NavigateNext(val goal: OnboardingGoal) : OnboardingEvent
}
