package com.nebulatech.lumi.data.model

import java.time.Instant

data class DailyLog(
    val id: String,
    val userId: String,
    val cycleId: String? = null,
    val logDate: String, // ISO Date: YYYY-MM-DD
    val cycleDay: Int? = null,
    val cyclePhase: CyclePhase? = null,
    val flowIntensity: FlowIntensityType? = null,
    val mood: MoodType? = null,
    val symptoms: List<DailySymptom> = emptyList(),
    val notes: String? = null,
    val createdAt: String = Instant.now().toString(),
    val updatedAt: String = Instant.now().toString(),
    val isSynced: Boolean = false
)

data class DailySymptom(
    val id: String,
    val dailyLogId: String,
    val userId: String,
    val logDate: String,
    val cycleId: String? = null,
    val cycleDay: Int? = null,
    val symptomKey: String,
    val symptomDisplayName: String,
    val isCustom: Boolean = false,
    val customSymptomId: String? = null,
    val createdAt: String = Instant.now().toString(),
    val isSynced: Boolean = false
)
