package com.nebulatech.lumi.data.mapper

import com.nebulatech.lumi.data.local.entity.DailyLogEntity
import com.nebulatech.lumi.data.local.entity.DailySymptomEntity
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.data.model.DailyLog
import com.nebulatech.lumi.data.model.DailySymptom
import com.nebulatech.lumi.data.model.FlowIntensityType
import com.nebulatech.lumi.data.model.MoodType

fun DailyLogEntity.toDailyLog(
    symptoms: List<DailySymptomEntity> = emptyList()
): DailyLog = DailyLog(
    id = id,
    userId = userId,
    cycleId = cycleId,
    logDate = logDate,
    cycleDay = cycleDay,
    cyclePhase = cyclePhase?.let { runCatching { CyclePhase.valueOf(it) }.getOrNull() },
    flowIntensity = flowIntensity?.let { runCatching { FlowIntensityType.valueOf(it) }.getOrNull() },
    mood = mood?.let { runCatching { MoodType.valueOf(it) }.getOrNull() },
    symptoms = symptoms.map { it.toDailySymptom() },
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isSynced = isSynced
)

fun DailyLog.toDailyLogEntity(): DailyLogEntity = DailyLogEntity(
    id = id,
    userId = userId,
    cycleId = cycleId,
    logDate = logDate,
    cycleDay = cycleDay,
    cyclePhase = cyclePhase?.name,
    flowIntensity = flowIntensity?.name,
    mood = mood?.name,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isSynced = isSynced
)

fun DailySymptomEntity.toDailySymptom(): DailySymptom = DailySymptom(
    id = id,
    dailyLogId = dailyLogId,
    userId = userId,
    logDate = logDate,
    cycleId = cycleId,
    cycleDay = cycleDay,
    symptomKey = symptomKey,
    symptomDisplayName = symptomDisplayName,
    isCustom = isCustom,
    customSymptomId = customSymptomId,
    createdAt = createdAt,
    isSynced = isSynced
)

fun DailySymptom.toDailySymptomEntity(): DailySymptomEntity = DailySymptomEntity(
    id = id,
    dailyLogId = dailyLogId,
    userId = userId,
    logDate = logDate,
    cycleId = cycleId,
    cycleDay = cycleDay,
    symptomKey = symptomKey,
    symptomDisplayName = symptomDisplayName,
    isCustom = isCustom,
    customSymptomId = customSymptomId,
    createdAt = createdAt,
    isSynced = isSynced
)
