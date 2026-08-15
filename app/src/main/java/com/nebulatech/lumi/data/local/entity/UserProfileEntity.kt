package com.nebulatech.lumi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_profiles",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"], unique = true)
    ]
)
data class UserProfileEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val age: Int? = null,
    val weight: Double? = null,
    val weightUnit: String = "KG",
    val cycleLength: Int = 28,
    val periodDuration: Int = 5,
    val primaryGoal: String = "TRACK_CYCLE",
    val notificationsEnabled: Boolean = true,
    val trackingStartedDate: String,
    val updatedAt: String,
    val isSynced: Boolean = false
)
