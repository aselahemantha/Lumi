package com.nebulatech.lumi.logging

// LoggingContract uses the bottom sheet's local enums to keep the ViewModel
// as the single conversion boundary between UI types and domain types.

import java.time.LocalDate

data class LoggingState(
    val isSaving: Boolean = false,
    val lastSaveError: String? = null
)

sealed interface LoggingAction {
    /** Triggered by LogFlowBottomSheet.onSaveLog */
    data class SaveFlowLog(
        val flow: FlowIntensity?,
        val mood: MoodItem?,
        val symptoms: Set<String>,
        val logDate: LocalDate = LocalDate.now()
    ) : LoggingAction

    /** Triggered by LogBBTBottomSheet.onSaveReading */
    data class SaveBbt(
        val temperatureStr: String,
        val disturbedSleep: Boolean,
        val feverIllness: Boolean
    ) : LoggingAction

    /** Triggered by LogLHTestBottomSheet.onSaveResult */
    data class SaveLhTest(
        val intensity: LHIntensity,
        val brand: String?
    ) : LoggingAction
}

sealed interface LoggingEvent {
    data object FlowSaved : LoggingEvent
    data object BbtSaved : LoggingEvent
    data object LhSaved : LoggingEvent
    data class SaveError(val message: String) : LoggingEvent
}
