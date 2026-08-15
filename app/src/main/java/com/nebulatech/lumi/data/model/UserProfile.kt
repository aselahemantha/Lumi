package com.nebulatech.lumi.data.model

import java.time.Instant

data class UserProfile(
    val id: String,
    val userId: String,
    val age: Int? = null,
    val weight: Double? = null,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val cycleLength: Int = 28,
    val periodDuration: Int = 5,
    val primaryGoal: PrimaryGoal = PrimaryGoal.TRACK_CYCLE,
    val notificationsEnabled: Boolean = true,
    val trackingStartedDate: String = Instant.now().toString(),
    val healthConditions: List<HealthConditionType> = emptyList(),
    val updatedAt: String = Instant.now().toString(),
    val isSynced: Boolean = false
)
