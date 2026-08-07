package com.nebulatech.lumi.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    private val _events = Channel<OnboardingEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.SelectGoal -> {
                _state.update { it.copy(selectedGoal = action.goal) }
            }
            is OnboardingAction.UpdateFirstDayOfLastPeriod -> {
                _state.update { it.copy(firstDayOfLastPeriod = action.date) }
            }
            is OnboardingAction.UpdatePeriodDuration -> {
                _state.update { it.copy(periodDuration = action.duration) }
            }
            is OnboardingAction.UpdateCycleLength -> {
                _state.update { it.copy(cycleLength = action.length) }
            }
            is OnboardingAction.UpdateAge -> {
                _state.update { it.copy(age = action.age.filter { char -> char.isDigit() }) }
            }
            is OnboardingAction.UpdateWeight -> {
                _state.update { it.copy(weight = action.weight.filter { char -> char.isDigit() || char == '.' }) }
            }
            is OnboardingAction.UpdateWeightUnit -> {
                _state.update { it.copy(weightUnit = action.unit) }
            }
            is OnboardingAction.ToggleCondition -> {
                _state.update { currentState ->
                    val updatedConditions = currentState.selectedConditions.toMutableSet()
                    if (action.condition == "None of the above") {
                        if (updatedConditions.contains(action.condition)) {
                            updatedConditions.remove(action.condition)
                        } else {
                            updatedConditions.clear()
                            updatedConditions.add(action.condition)
                        }
                    } else {
                        updatedConditions.remove("None of the above")
                        if (updatedConditions.contains(action.condition)) {
                            updatedConditions.remove(action.condition)
                        } else {
                            updatedConditions.add(action.condition)
                        }
                    }
                    currentState.copy(selectedConditions = updatedConditions)
                }
            }
            OnboardingAction.ClickBack -> {
                when (state.value.currentStep) {
                    OnboardingStep.WELCOME -> {
                        viewModelScope.launch {
                            _events.send(OnboardingEvent.NavigateBack)
                        }
                    }
                    OnboardingStep.SELECT_GOAL -> {
                        _state.update { it.copy(currentStep = OnboardingStep.WELCOME) }
                    }
                    OnboardingStep.CORE_DATA -> {
                        _state.update { it.copy(currentStep = OnboardingStep.SELECT_GOAL) }
                    }
                    OnboardingStep.HEALTH_PROFILE -> {
                        _state.update { it.copy(currentStep = OnboardingStep.CORE_DATA) }
                    }
                }
            }
            OnboardingAction.ClickContinue -> {
                when (state.value.currentStep) {
                    OnboardingStep.WELCOME -> {
                        _state.update { it.copy(currentStep = OnboardingStep.SELECT_GOAL) }
                    }
                    OnboardingStep.SELECT_GOAL -> {
                        if (state.value.selectedGoal != null) {
                            _state.update { it.copy(currentStep = OnboardingStep.CORE_DATA) }
                        }
                    }
                    OnboardingStep.CORE_DATA -> {
                        _state.update { it.copy(currentStep = OnboardingStep.HEALTH_PROFILE) }
                    }
                    OnboardingStep.HEALTH_PROFILE -> {
                        state.value.selectedGoal?.let { goal ->
                            viewModelScope.launch {
                                _events.send(OnboardingEvent.NavigateNext(goal))
                            }
                        }
                    }
                }
            }
        }
    }
}
