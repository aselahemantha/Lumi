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
            OnboardingAction.ClickBack -> {
                viewModelScope.launch {
                    _events.send(OnboardingEvent.NavigateBack)
                }
            }
            OnboardingAction.ClickContinue -> {
                state.value.selectedGoal?.let { goal ->
                    viewModelScope.launch {
                        _events.send(OnboardingEvent.NavigateNext(goal))
                    }
                }
            }
        }
    }
}
