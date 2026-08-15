package com.nebulatech.lumi.insights

import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.insights.components.CycleHistoryItem

data class SymptomTrendPoint(
    val cycleDay: Int,
    val dayRatio: Float,
    val symptomName: String,
    val phaseName: String
)

data class InsightsState(
    val isLoading: Boolean = true,
    val cycleHistory: List<CycleHistoryItem> = emptyList(),
    val currentCycleDay: Int = 1,
    val cycleLength: Int = 28,
    val currentPhase: CyclePhase = CyclePhase.FOLLICULAR,
    val loggedSymptomPoints: List<SymptomTrendPoint> = emptyList(),
    val dynamicInsightText: String = "",
    val hasCloudSync: Boolean = false
)

sealed interface InsightsAction {
    data class RefreshData(val userId: String) : InsightsAction
}
