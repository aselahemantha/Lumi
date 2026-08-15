package com.nebulatech.lumi.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.data.repository.CycleRepository
import com.nebulatech.lumi.data.repository.RoomUserRepository
import com.nebulatech.lumi.data.repository.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val userRepository: UserRepository,
    private val cycleRepository: CycleRepository
) : ViewModel() {

    private val userId = RoomUserRepository.DEFAULT_LOCAL_USER_ID
    private val today = LocalDate.now()

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    val state: StateFlow<HomeState> = combine(
        userRepository.getCurrentUser(),
        cycleRepository.getCurrentCycle(userId),
        cycleRepository.getCycleDay(userId, today),
        cycleRepository.getAverageCycleLength(userId),
        cycleRepository.getCurrentPhase(userId, today)
    ) { user, cycle, cycleDay, avgCycleLength, phase ->
        if (user == null) {
            viewModelScope.launch { _events.send(HomeEvent.NavigateToOnboarding) }
            return@combine HomeState(isLoading = false)
        }

        val avgPeriodLength = 5 // Will be pulled from profile via a separate flow if needed
        val progressRatio = (cycleDay.toFloat() / avgCycleLength.toFloat()).coerceIn(0f, 1f)
        val subLabel = buildSubLabel(phase, cycleDay, avgCycleLength)

        HomeState(
            isLoading = false,
            layoutType = phase.toLayoutType(),
            cycleDay = cycleDay,
            cycleLength = avgCycleLength,
            periodLength = avgPeriodLength,
            progressRatio = progressRatio,
            subLabelText = subLabel,
            userName = user.name,
            hasCycle = cycle != null,
            currentPhase = phase
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState()
    )

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.StartNewPeriod -> {
                viewModelScope.launch {
                    cycleRepository.startNewCycle(userId, today)
                }
            }
        }
    }

    private fun CyclePhase.toLayoutType(): HomeLayoutType = when (this) {
        CyclePhase.MENSTRUATION -> HomeLayoutType.CYCLE_RING
        CyclePhase.FOLLICULAR -> HomeLayoutType.CYCLE_RING
        CyclePhase.FERTILE_WINDOW -> HomeLayoutType.FERTILITY_DASHBOARD
        CyclePhase.LUTEAL -> HomeLayoutType.CYCLE_RING
        CyclePhase.LATE_LUTEAL -> HomeLayoutType.SYMPTOM_GRID
    }

    private fun buildSubLabel(phase: CyclePhase, cycleDay: Int, cycleLength: Int): String {
        return when (phase) {
            CyclePhase.MENSTRUATION -> "Day $cycleDay of your period"
            CyclePhase.FOLLICULAR -> {
                val fertileStart = maxOf(cycleLength - 14 - 3, 1)
                val daysUntil = maxOf(fertileStart - cycleDay, 1)
                "Fertile window in ~$daysUntil days"
            }
            CyclePhase.FERTILE_WINDOW -> "Peak fertility window"
            CyclePhase.LUTEAL -> {
                val daysUntil = maxOf(cycleLength - cycleDay, 1)
                "Period in ~$daysUntil days"
            }
            CyclePhase.LATE_LUTEAL -> {
                val daysUntil = maxOf(cycleLength - cycleDay, 1)
                "Period in ~$daysUntil days"
            }
        }
    }
}
