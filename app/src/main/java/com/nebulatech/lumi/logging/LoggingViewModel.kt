package com.nebulatech.lumi.logging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nebulatech.lumi.data.model.BbtSource
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.data.model.FlowIntensityType
import com.nebulatech.lumi.data.model.LhIntensityType
import com.nebulatech.lumi.data.model.MoodType
import com.nebulatech.lumi.data.repository.BbtRepository
import com.nebulatech.lumi.data.repository.CycleRepository
import com.nebulatech.lumi.data.repository.DailyLogRepository
import com.nebulatech.lumi.data.repository.LhTestRepository
import com.nebulatech.lumi.data.repository.RoomUserRepository
import com.nebulatech.lumi.core.domain.Result
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class LoggingViewModel(
    private val dailyLogRepository: DailyLogRepository,
    private val bbtRepository: BbtRepository,
    private val lhTestRepository: LhTestRepository,
    private val cycleRepository: CycleRepository
) : ViewModel() {

    private val userId = RoomUserRepository.DEFAULT_LOCAL_USER_ID
    private val today = LocalDate.now()

    private val _state = MutableStateFlow(LoggingState())
    val state = _state.asStateFlow()

    private val _events = Channel<LoggingEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: LoggingAction) {
        when (action) {
            is LoggingAction.SaveFlowLog -> saveFlowLog(action)
            is LoggingAction.SaveBbt -> saveBbt(action)
            is LoggingAction.SaveLhTest -> saveLhTest(action)
        }
    }

    private fun saveFlowLog(action: LoggingAction.SaveFlowLog) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val phase = cycleRepository.getCurrentPhase(userId, today).first()
                val cycleDay = cycleRepository.getCycleDay(userId, today).first()
                val avgCycleLength = cycleRepository.getAverageCycleLength(userId).first()

                // If period is predicted/overdue and user logs flow, auto-start a new cycle.
                // This confirms Day 1 MENSTRUATION instead of logging against the old stale cycle.
                val currentCycleId: String?
                if (phase == CyclePhase.PERIOD_PREDICTED ||
                    (phase == CyclePhase.LATE_LUTEAL && cycleDay >= avgCycleLength)) {
                    val newCycleResult = cycleRepository.startNewCycle(userId, today)
                    currentCycleId = (newCycleResult as? Result.Success)?.data?.id
                } else {
                    currentCycleId = cycleRepository.getCurrentCycle(userId).first()?.id
                }

                // Re-read after potentially starting new cycle (cycleDay will now be 1)
                val updatedCycleDay = cycleRepository.getCycleDay(userId, today).first()
                val updatedPhase = cycleRepository.getCurrentPhase(userId, today).first()

                val result = dailyLogRepository.saveFlowLog(
                    userId = userId,
                    date = today,
                    flowIntensity = action.flow?.toDomainType(),
                    mood = action.mood?.toDomainType(),
                    symptoms = action.symptoms,
                    cycleId = currentCycleId,
                    cycleDay = updatedCycleDay,
                    cyclePhase = updatedPhase
                )
                when (result) {
                    is Result.Success -> _events.send(LoggingEvent.FlowSaved)
                    is Result.Error -> _events.send(LoggingEvent.SaveError("Failed to save log"))
                }
            } catch (e: Exception) {
                _events.send(LoggingEvent.SaveError(e.message ?: "Unknown error"))
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun saveBbt(action: LoggingAction.SaveBbt) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val temp = action.temperatureStr.toDoubleOrNull() ?: return@launch
                val currentCycle = cycleRepository.getCurrentCycle(userId).first()
                val cycleDay = cycleRepository.getCycleDay(userId, today).first()

                val result = bbtRepository.saveBbtReading(
                    userId = userId,
                    date = today,
                    temperature = temp,
                    temperatureUnit = "F",
                    readingTime = null,
                    cycleId = currentCycle?.id,
                    cycleDay = cycleDay,
                    disturbedSleep = action.disturbedSleep,
                    feverIllness = action.feverIllness,
                    source = BbtSource.MANUAL
                )
                when (result) {
                    is Result.Success -> _events.send(LoggingEvent.BbtSaved)
                    is Result.Error -> _events.send(LoggingEvent.SaveError("Failed to save BBT"))
                }
            } catch (e: Exception) {
                _events.send(LoggingEvent.SaveError(e.message ?: "Unknown error"))
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun saveLhTest(action: LoggingAction.SaveLhTest) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val currentCycle = cycleRepository.getCurrentCycle(userId).first()
                val cycleDay = cycleRepository.getCycleDay(userId, today).first()

                val result = lhTestRepository.saveLhTest(
                    userId = userId,
                    date = today,
                    intensity = action.intensity.toDomainType(),
                    testBrand = action.brand?.takeIf { it.isNotBlank() },
                    cycleId = currentCycle?.id,
                    cycleDay = cycleDay
                )
                when (result) {
                    is Result.Success -> _events.send(LoggingEvent.LhSaved)
                    is Result.Error -> _events.send(LoggingEvent.SaveError("Failed to save LH test"))
                }
            } catch (e: Exception) {
                _events.send(LoggingEvent.SaveError(e.message ?: "Unknown error"))
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    // ── Enum Mappers ──────────────────────────────────────────────────────────

    private fun FlowIntensity.toDomainType(): FlowIntensityType = when (this) {
        FlowIntensity.LIGHT -> FlowIntensityType.LIGHT
        FlowIntensity.MEDIUM -> FlowIntensityType.MEDIUM
        FlowIntensity.HEAVY -> FlowIntensityType.HEAVY
    }

    private fun MoodItem.toDomainType(): MoodType = when (this) {
        MoodItem.CALM -> MoodType.CALM
        MoodItem.ENERGETIC -> MoodType.ENERGETIC
        MoodItem.SENSITIVE -> MoodType.SENSITIVE
        MoodItem.TIRED -> MoodType.TIRED
    }

    private fun LHIntensity.toDomainType(): LhIntensityType = when (this) {
        LHIntensity.LOW -> LhIntensityType.LOW
        LHIntensity.HIGH -> LhIntensityType.HIGH
        LHIntensity.PEAK -> LhIntensityType.PEAK
    }
}
