package com.nebulatech.lumi.data.model

import java.time.Instant

data class Cycle(
    val id: String,
    val userId: String,
    val cycleNumber: Int,
    val startDate: String, // ISO Date: YYYY-MM-DD
    val endDate: String? = null,
    val cycleLength: Int? = null,
    val periodLength: Int? = null,
    val ovulationDate: String? = null,
    val isCurrent: Boolean = true,
    val isRegular: Boolean? = null,
    val notes: String? = null,
    val createdAt: String = Instant.now().toString(),
    val updatedAt: String = Instant.now().toString(),
    val isSynced: Boolean = false
)

data class PastCycleInput(
    val startDate: java.time.LocalDate,
    val cycleLength: Int = 28,
    val periodDuration: Int = 5
)
