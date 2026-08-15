package com.nebulatech.lumi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cycles",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["startDate"])
    ]
)
data class CycleEntity(
    @PrimaryKey
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
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false
)
