package com.nebulatech.lumi.data.model

import java.time.Instant

data class BbtReading(
    val id: String,
    val userId: String,
    val cycleId: String? = null,
    val dailyLogId: String? = null,
    val readingDate: String, // ISO Date: YYYY-MM-DD
    val readingTime: String? = null, // HH:mm
    val temperature: Double,
    val temperatureUnit: String = "F",
    val cycleDay: Int? = null,
    val disturbedSleep: Boolean = false,
    val feverIllness: Boolean = false,
    val source: BbtSource = BbtSource.MANUAL,
    val createdAt: String = Instant.now().toString(),
    val updatedAt: String = Instant.now().toString(),
    val isSynced: Boolean = false
)
