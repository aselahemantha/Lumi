package com.nebulatech.lumi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_symptoms",
    foreignKeys = [
        ForeignKey(
            entity = DailyLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["dailyLogId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["dailyLogId"]),
        Index(value = ["userId"]),
        Index(value = ["logDate"]),
        Index(value = ["symptomKey"])
    ]
)
data class DailySymptomEntity(
    @PrimaryKey
    val id: String,
    val dailyLogId: String,
    val userId: String,
    val logDate: String, // ISO Date: YYYY-MM-DD
    val cycleId: String? = null,
    val cycleDay: Int? = null,
    val symptomKey: String, // CRAMPS, BLOATING, HEADACHE, ACNE, BACKACHE, etc.
    val symptomDisplayName: String,
    val isCustom: Boolean = false,
    val customSymptomId: String? = null,
    val createdAt: String,
    val isSynced: Boolean = false
)
