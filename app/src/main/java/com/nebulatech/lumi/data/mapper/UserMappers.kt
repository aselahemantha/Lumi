package com.nebulatech.lumi.data.mapper

import com.nebulatech.lumi.data.local.entity.HealthConditionEntity
import com.nebulatech.lumi.data.local.entity.UserEntity
import com.nebulatech.lumi.data.local.entity.UserProfileEntity
import com.nebulatech.lumi.data.model.HealthConditionType
import com.nebulatech.lumi.data.model.PrimaryGoal
import com.nebulatech.lumi.data.model.User
import com.nebulatech.lumi.data.model.UserProfile
import com.nebulatech.lumi.data.model.WeightUnit
import java.time.Instant

fun UserEntity.toUser(): User = User(
    id = id,
    name = name,
    email = email,
    supabaseUid = supabaseUid,
    isPremium = isPremium,
    memberSince = memberSince,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isSynced = isSynced
)

fun User.toUserEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    supabaseUid = supabaseUid,
    isPremium = isPremium,
    memberSince = memberSince,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isSynced = isSynced
)

fun UserProfileEntity.toUserProfile(
    conditions: List<HealthConditionEntity> = emptyList()
): UserProfile = UserProfile(
    id = id,
    userId = userId,
    age = age,
    weight = weight,
    weightUnit = runCatching { WeightUnit.valueOf(weightUnit.uppercase()) }.getOrDefault(WeightUnit.KG),
    cycleLength = cycleLength,
    periodDuration = periodDuration,
    primaryGoal = runCatching { PrimaryGoal.valueOf(primaryGoal) }.getOrDefault(PrimaryGoal.TRACK_CYCLE),
    notificationsEnabled = notificationsEnabled,
    trackingStartedDate = trackingStartedDate,
    healthConditions = conditions.mapNotNull { entity ->
        runCatching { HealthConditionType.valueOf(entity.condition) }.getOrNull()
    },
    updatedAt = updatedAt,
    isSynced = isSynced
)

fun UserProfile.toUserProfileEntity(): UserProfileEntity = UserProfileEntity(
    id = id,
    userId = userId,
    age = age,
    weight = weight,
    weightUnit = weightUnit.name,
    cycleLength = cycleLength,
    periodDuration = periodDuration,
    primaryGoal = primaryGoal.name,
    notificationsEnabled = notificationsEnabled,
    trackingStartedDate = trackingStartedDate,
    updatedAt = updatedAt,
    isSynced = isSynced
)
