package com.nebulatech.lumi.onboarding

import java.time.LocalDate
import kotlinx.serialization.Serializable

@Serializable
object OnboardingRoute

enum class OnboardingStep {
    WELCOME,
    SELECT_GOAL,
    CORE_DATA,
    HEALTH_PROFILE
}

enum class OnboardingGoal {
    TRACK_CYCLE,
    UNDERSTAND_SYMPTOMS,
    OPTIMIZE_FERTILITY
}

data class ManualPastCycle(
    val startDate: LocalDate,
    val cycleLength: Int = 28,
    val periodDuration: Int = 5
)

data class OnboardingState(
    val currentStep: OnboardingStep = OnboardingStep.WELCOME,
    val selectedGoal: OnboardingGoal? = null,
    val firstDayOfLastPeriod: LocalDate = LocalDate.now(),
    val periodDuration: Int = 5,
    val cycleLength: Int = 28,
    val customPastCycles: List<ManualPastCycle>? = null,
    val age: String = "",
    val weight: String = "",
    val weightUnit: String = "kg",
    val selectedConditions: Set<String> = emptySet(),
    val name: String = "",
    val isSaving: Boolean = false
)

sealed interface OnboardingAction {
    data class SelectGoal(val goal: OnboardingGoal) : OnboardingAction
    data object ClickBack : OnboardingAction
    data object ClickContinue : OnboardingAction
    data class UpdateFirstDayOfLastPeriod(val date: LocalDate) : OnboardingAction
    data class UpdatePeriodDuration(val duration: Int) : OnboardingAction
    data class UpdateCycleLength(val length: Int) : OnboardingAction
    data class UpdateCustomPastCycles(val cycles: List<ManualPastCycle>?) : OnboardingAction
    data class UpdateAge(val age: String) : OnboardingAction
    data class UpdateWeight(val weight: String) : OnboardingAction
    data class UpdateWeightUnit(val unit: String) : OnboardingAction
    data class ToggleCondition(val condition: String) : OnboardingAction
    data class UpdateName(val name: String) : OnboardingAction
}

sealed interface OnboardingEvent {
    data object NavigateBack : OnboardingEvent
    data class NavigateNext(val goal: OnboardingGoal) : OnboardingEvent
}
