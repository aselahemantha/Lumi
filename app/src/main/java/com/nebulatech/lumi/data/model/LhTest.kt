package com.nebulatech.lumi.data.model

import java.time.Instant

data class LhTest(
    val id: String,
    val userId: String,
    val cycleId: String? = null,
    val dailyLogId: String? = null,
    val testDate: String, // ISO Date: YYYY-MM-DD
    val cycleDay: Int? = null,
    val intensity: LhIntensityType,
    val testBrand: String? = null,
    val photoLocalPath: String? = null,
    val photoRemoteUrl: String? = null,
    val createdAt: String = Instant.now().toString(),
    val updatedAt: String = Instant.now().toString(),
    val isSynced: Boolean = false
)

data class CustomSymptom(
    val id: String,
    val userId: String,
    val name: String,
    val iconName: String? = null,
    val category: SymptomCategoryType? = null,
    val sortOrder: Int = 0,
    val isActive: Boolean = true,
    val createdAt: String = Instant.now().toString(),
    val isSynced: Boolean = false
)
