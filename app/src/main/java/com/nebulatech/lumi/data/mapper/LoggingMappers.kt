package com.nebulatech.lumi.data.mapper

import com.nebulatech.lumi.data.local.entity.BbtReadingEntity
import com.nebulatech.lumi.data.local.entity.CustomSymptomEntity
import com.nebulatech.lumi.data.local.entity.LhTestEntity
import com.nebulatech.lumi.data.model.BbtReading
import com.nebulatech.lumi.data.model.BbtSource
import com.nebulatech.lumi.data.model.CustomSymptom
import com.nebulatech.lumi.data.model.LhIntensityType
import com.nebulatech.lumi.data.model.LhTest
import com.nebulatech.lumi.data.model.SymptomCategoryType

fun BbtReadingEntity.toBbtReading(): BbtReading = BbtReading(
    id = id,
    userId = userId,
    cycleId = cycleId,
    dailyLogId = dailyLogId,
    readingDate = readingDate,
    readingTime = readingTime,
    temperature = temperature,
    temperatureUnit = temperatureUnit,
    cycleDay = cycleDay,
    disturbedSleep = disturbedSleep,
    feverIllness = feverIllness,
    source = runCatching { BbtSource.valueOf(source) }.getOrDefault(BbtSource.MANUAL),
    createdAt = createdAt,
    updatedAt = updatedAt,
    isSynced = isSynced
)

fun BbtReading.toBbtReadingEntity(): BbtReadingEntity = BbtReadingEntity(
    id = id,
    userId = userId,
    cycleId = cycleId,
    dailyLogId = dailyLogId,
    readingDate = readingDate,
    readingTime = readingTime,
    temperature = temperature,
    temperatureUnit = temperatureUnit,
    cycleDay = cycleDay,
    disturbedSleep = disturbedSleep,
    feverIllness = feverIllness,
    source = source.name,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isSynced = isSynced
)

fun LhTestEntity.toLhTest(): LhTest = LhTest(
    id = id,
    userId = userId,
    cycleId = cycleId,
    dailyLogId = dailyLogId,
    testDate = testDate,
    cycleDay = cycleDay,
    intensity = runCatching { LhIntensityType.valueOf(intensity) }.getOrDefault(LhIntensityType.LOW),
    testBrand = testBrand,
    photoLocalPath = photoLocalPath,
    photoRemoteUrl = photoRemoteUrl,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isSynced = isSynced
)

fun LhTest.toLhTestEntity(): LhTestEntity = LhTestEntity(
    id = id,
    userId = userId,
    cycleId = cycleId,
    dailyLogId = dailyLogId,
    testDate = testDate,
    cycleDay = cycleDay,
    intensity = intensity.name,
    testBrand = testBrand,
    photoLocalPath = photoLocalPath,
    photoRemoteUrl = photoRemoteUrl,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isSynced = isSynced
)

fun CustomSymptomEntity.toCustomSymptom(): CustomSymptom = CustomSymptom(
    id = id,
    userId = userId,
    name = name,
    iconName = iconName,
    category = category?.let { runCatching { SymptomCategoryType.valueOf(it) }.getOrNull() },
    sortOrder = sortOrder,
    isActive = isActive,
    createdAt = createdAt,
    isSynced = isSynced
)

fun CustomSymptom.toCustomSymptomEntity(): CustomSymptomEntity = CustomSymptomEntity(
    id = id,
    userId = userId,
    name = name,
    iconName = iconName,
    category = category?.name,
    sortOrder = sortOrder,
    isActive = isActive,
    createdAt = createdAt,
    isSynced = isSynced
)
