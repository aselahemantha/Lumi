package com.nebulatech.lumi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lh_tests",
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
        Index(value = ["testDate"])
    ]
)
data class LhTestEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val cycleId: String? = null,
    val dailyLogId: String? = null,
    val testDate: String, // ISO Date: YYYY-MM-DD
    val cycleDay: Int? = null,
    val intensity: String, // LOW, HIGH, PEAK
    val testBrand: String? = null,
    val photoLocalPath: String? = null,
    val photoRemoteUrl: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false
)
