package com.nebulatech.lumi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bbt_readings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CycleEntity::class,
            parentColumns = ["id"],
            childColumns = ["cycleId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = DailyLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["dailyLogId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["cycleId"]),
        Index(value = ["dailyLogId"]),
        Index(value = ["readingDate"])
    ]
)
data class BbtReadingEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val cycleId: String? = null,
    val dailyLogId: String? = null,
    val readingDate: String, // ISO Date: YYYY-MM-DD
    val readingTime: String? = null, // e.g., "07:30"
    val temperature: Double,
    val temperatureUnit: String = "F",
    val cycleDay: Int? = null,
    val disturbedSleep: Boolean = false,
    val feverIllness: Boolean = false,
    val source: String = "MANUAL", // MANUAL, OURA, GARMIN, APPLE_HEALTH
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false
)
