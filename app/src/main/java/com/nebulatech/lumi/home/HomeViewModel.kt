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
        combine(
            userRepository.getCurrentUser(),
            cycleRepository.getCurrentCycle(userId),
            cycleRepository.getCycleDay(userId, today)
        ) { user, cycle, cycleDay -> Triple(user, cycle, cycleDay) },
        combine(
            cycleRepository.getAverageCycleLength(userId),
            cycleRepository.getAveragePeriodLength(userId),
            cycleRepository.getCurrentPhase(userId, today)
        ) { avgCycle, avgPeriod, phase -> Triple(avgCycle, avgPeriod, phase) }
    ) { (user, cycle, cycleDay), (avgCycleLength, avgPeriodLength, phase) ->
        if (user == null) {
            viewModelScope.launch { _events.send(HomeEvent.NavigateToOnboarding) }
            return@combine HomeState(isLoading = false)
        }

        val progressRatio = (cycleDay.toFloat() / avgCycleLength.toFloat()).coerceIn(0f, 1f)
        val subLabel = buildSubLabel(phase, cycleDay, avgCycleLength, avgPeriodLength)
        val (insightTitle, insightText) = buildInsight(phase, cycleDay, avgCycleLength)

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
            currentPhase = phase,
            insightTitle = insightTitle,
            insightText = insightText,
            isPeriodPredicted = phase == CyclePhase.PERIOD_PREDICTED
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
            // ConfirmPeriodStart is handled at the UI level via LoggingViewModel;
            // the flow sheet is shown and on save, LoggingViewModel auto-starts the new cycle.
            HomeAction.ConfirmPeriodStart -> Unit
        }
    }

    private fun CyclePhase.toLayoutType(): HomeLayoutType = when (this) {
        CyclePhase.MENSTRUATION -> HomeLayoutType.CYCLE_RING
        CyclePhase.FOLLICULAR -> HomeLayoutType.CYCLE_RING
        CyclePhase.FERTILE_WINDOW -> HomeLayoutType.FERTILITY_DASHBOARD
        CyclePhase.LUTEAL -> HomeLayoutType.CYCLE_RING
        CyclePhase.LATE_LUTEAL -> HomeLayoutType.SYMPTOM_GRID
        CyclePhase.PERIOD_PREDICTED -> HomeLayoutType.CYCLE_RING
    }

    private fun buildSubLabel(phase: CyclePhase, cycleDay: Int, cycleLength: Int, periodLength: Int): String {
        return when (phase) {
            CyclePhase.MENSTRUATION -> "Day $cycleDay of your period"
            CyclePhase.FOLLICULAR -> {
                val ovulationDay = cycleLength - 14
                val fertileStart = maxOf(ovulationDay - 3, periodLength + 1)
                val daysUntil = maxOf(fertileStart - cycleDay, 1)
                "Fertile window in ~$daysUntil days"
            }
            CyclePhase.FERTILE_WINDOW -> "Peak fertility window"
            CyclePhase.LUTEAL, CyclePhase.LATE_LUTEAL -> {
                val daysUntil = maxOf(cycleLength - cycleDay, 1)
                "Period in ~$daysUntil days"
            }
            CyclePhase.PERIOD_PREDICTED -> "Period expected · Log flow to confirm"
        }
    }

    private fun buildInsight(
        phase: CyclePhase,
        cycleDay: Int,
        cycleLength: Int
    ): Pair<String, String> {
        return when (phase) {
            CyclePhase.MENSTRUATION -> {
                "Menstrual Phase Insight" to "Estrogen and progesterone are at baseline. Prioritize iron-rich foods, restorative hydration, and gentle movement today."
            }
            CyclePhase.FOLLICULAR -> {
                "Follicular Phase Insight" to "Estrogen is rising steadily. Your natural energy and cognitive focus are peaking—ideal for high-intensity training and creative projects."
            }
            CyclePhase.FERTILE_WINDOW -> {
                "Peak Fertility Window" to "Your luteinizing hormone (LH) is surging. Likelihood of ovulation is peaking over the next 24–48 hours."
            }
            CyclePhase.LUTEAL -> {
                "Luteal Phase Insight" to "Progesterone is the dominant hormone right now. Consider swapping high-intensity cardio for calming yoga or walking to balance cortisol."
            }
            CyclePhase.LATE_LUTEAL -> {
                "Pre-Menstrual Insight" to "Progesterone is tapering down before your period. Staying well-hydrated and increasing magnesium intake can help prevent PMS headaches."
            }
            CyclePhase.PERIOD_PREDICTED -> {
                "Period Expected" to "Your period is predicted to start today or very soon. Tap the button below to log your flow and confirm the start of your new cycle."
            }
        }
    }
}
