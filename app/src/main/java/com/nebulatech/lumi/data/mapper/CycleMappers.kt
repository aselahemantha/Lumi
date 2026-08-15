package com.nebulatech.lumi.data.mapper

import com.nebulatech.lumi.data.local.entity.CycleEntity
import com.nebulatech.lumi.data.model.Cycle

fun CycleEntity.toCycle(): Cycle = Cycle(
    id = id,
    userId = userId,
    cycleNumber = cycleNumber,
    startDate = startDate,
    endDate = endDate,
    cycleLength = cycleLength,
    periodLength = periodLength,
    ovulationDate = ovulationDate,
    isCurrent = isCurrent,
    isRegular = isRegular,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isSynced = isSynced
)

fun Cycle.toCycleEntity(): CycleEntity = CycleEntity(
    id = id,
    userId = userId,
    cycleNumber = cycleNumber,
    startDate = startDate,
    endDate = endDate,
    cycleLength = cycleLength,
    periodLength = periodLength,
    ovulationDate = ovulationDate,
    isCurrent = isCurrent,
    isRegular = isRegular,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isSynced = isSynced
)
