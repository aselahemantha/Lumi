package com.nebulatech.lumi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_logs",
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
        )
    ],
    indices = [
        Index(value = ["userId", "logDate"], unique = true),
        Index(value = ["cycleId"]),
        Index(value = ["logDate"])
    ]
)
data class DailyLogEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val cycleId: String? = null,
    val logDate: String, // ISO Date: YYYY-MM-DD
    val cycleDay: Int? = null,
    val cyclePhase: String? = null, // MENSTRUATION, FOLLICULAR, FERTILE_WINDOW, LUTEAL, LATE_LUTEAL
    val flowIntensity: String? = null, // LIGHT, MEDIUM, HEAVY, SPOTTING, NONE
    val mood: String? = null, // CALM, ENERGETIC, SENSITIVE, TIRED
    val notes: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false
)
