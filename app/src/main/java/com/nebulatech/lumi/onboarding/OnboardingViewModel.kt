package com.nebulatech.lumi.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nebulatech.lumi.core.domain.Result
import com.nebulatech.lumi.data.model.Cycle
import com.nebulatech.lumi.data.model.HealthConditionType
import com.nebulatech.lumi.data.model.PrimaryGoal
import com.nebulatech.lumi.data.model.UserProfile
import com.nebulatech.lumi.data.model.WeightUnit
import com.nebulatech.lumi.data.repository.CycleRepository
import com.nebulatech.lumi.data.repository.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class OnboardingViewModel(
    private val userRepository: UserRepository,
    private val cycleRepository: CycleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

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
            is OnboardingAction.UpdateCustomPastCycles -> {
                _state.update { it.copy(customPastCycles = action.cycles) }
            }
            is OnboardingAction.UpdateAge -> {
                _state.update { it.copy(age = action.age) }
            }
            is OnboardingAction.UpdateWeight -> {
                _state.update { it.copy(weight = action.weight) }
            }
            is OnboardingAction.UpdateWeightUnit -> {
                _state.update { it.copy(weightUnit = action.unit) }
            }
            is OnboardingAction.ToggleCondition -> {
                _state.update { currentState ->
                    val updatedConditions = currentState.selectedConditions.toMutableSet()
                    if (action.condition.equals("None of the above", ignoreCase = true)) {
                        updatedConditions.clear()
                        updatedConditions.add(action.condition)
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
            is OnboardingAction.UpdateName -> {
                _state.update { it.copy(name = action.name) }
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
                            persistAndNavigate(goal)
                        }
                    }
                }
            }
        }
    }

    private fun persistAndNavigate(goal: OnboardingGoal) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val s = state.value
            val now = Instant.now().toString()

            // 1. Create or get user
            val userResult = userRepository.getOrCreateUser(
                name = s.name.ifBlank { "Lumi User" }
            )
            if (userResult is Result.Error) {
                _state.update { it.copy(isSaving = false) }
                return@launch
            }
            val user = (userResult as Result.Success).data

            // 2. Save user profile
            val profile = UserProfile(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                age = s.age.toIntOrNull(),
                weight = s.weight.toDoubleOrNull(),
                weightUnit = if (s.weightUnit.lowercase() == "lbs") WeightUnit.LBS else WeightUnit.KG,
                cycleLength = s.cycleLength,
                periodDuration = s.periodDuration,
                primaryGoal = goal.toPrimaryGoal(),
                notificationsEnabled = true,
                trackingStartedDate = now,
                healthConditions = s.selectedConditions.mapNotNull { it.toHealthCondition() },
                updatedAt = now
            )
            val profileResult = userRepository.saveUserProfile(profile)
            if (profileResult is Result.Error) {
                _state.update { it.copy(isSaving = false) }
                return@launch
            }

            // 3. Seed 3 months of baseline historical cycles (or custom manual past cycles) + start current active cycle
            val manualCycles = s.customPastCycles
            if (manualCycles != null && manualCycles.size == 3) {
                for (i in 0 until 3) {
                    val c = manualCycles[2 - i]
                    val cycleStart = c.startDate
                    val cycleEnd = cycleStart.plusDays((c.cycleLength - 1).toLong())
                    val ovulationDay = maxOf(c.cycleLength - 14, c.periodDuration + 1)
                    val ovulationDate = cycleStart.plusDays(ovulationDay.toLong()).toString()
                    val histCycle = Cycle(
                        id = UUID.randomUUID().toString(),
                        userId = user.id,
                        cycleNumber = i + 1,
                        startDate = cycleStart.toString(),
                        endDate = cycleEnd.toString(),
                        cycleLength = c.cycleLength,
                        periodLength = c.periodDuration,
                        ovulationDate = ovulationDate,
                        isCurrent = false,
                        isRegular = true,
                        notes = "Manual historical cycle entry",
                        createdAt = now,
                        updatedAt = now
                    )
                    cycleRepository.updateCycle(histCycle)
                }
                val currentCycle = Cycle(
                    id = UUID.randomUUID().toString(),
                    userId = user.id,
                    cycleNumber = 4,
                    startDate = s.firstDayOfLastPeriod.toString(),
                    endDate = null,
                    cycleLength = null,
                    periodLength = s.periodDuration,
                    ovulationDate = null,
                    isCurrent = true,
                    isRegular = true,
                    notes = null,
                    createdAt = now,
                    updatedAt = now
                )
                cycleRepository.updateCycle(currentCycle)
            } else {
                cycleRepository.seedHistoricalCycles(
                    userId = user.id,
                    currentCycleStartDate = s.firstDayOfLastPeriod,
                    cycleLength = s.cycleLength,
                    periodDuration = s.periodDuration,
                    numberOfPastCycles = 3
                )
            }

            _state.update { it.copy(isSaving = false) }
            _events.send(OnboardingEvent.NavigateNext(goal))
        }
    }

    private fun OnboardingGoal.toPrimaryGoal(): PrimaryGoal = when (this) {
        OnboardingGoal.TRACK_CYCLE -> PrimaryGoal.TRACK_CYCLE
        OnboardingGoal.UNDERSTAND_SYMPTOMS -> PrimaryGoal.UNDERSTAND_SYMPTOMS
        OnboardingGoal.OPTIMIZE_FERTILITY -> PrimaryGoal.OPTIMIZE_FERTILITY
    }

    private fun String.toHealthCondition(): HealthConditionType? = when (this.uppercase()) {
        "PCOS" -> HealthConditionType.PCOS
        "ENDOMETRIOSIS" -> HealthConditionType.ENDOMETRIOSIS
        "THYROID" -> HealthConditionType.THYROID
        "NONE OF THE ABOVE" -> null
        else -> null
    }
}
