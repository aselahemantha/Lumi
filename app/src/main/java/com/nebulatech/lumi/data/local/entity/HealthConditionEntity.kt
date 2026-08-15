package com.nebulatech.lumi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_health_conditions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"])
    ]
)
data class HealthConditionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val condition: String, // PCOS, ENDOMETRIOSIS, THYROID, NONE
    val createdAt: String,
    val isSynced: Boolean = false
)
