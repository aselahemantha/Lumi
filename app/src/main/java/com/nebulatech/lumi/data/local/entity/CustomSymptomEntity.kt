package com.nebulatech.lumi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "custom_symptoms",
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
data class CustomSymptomEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val iconName: String? = null,
    val category: String? = null, // PHYSICAL, EMOTIONAL, DIGESTIVE, SKIN, OTHER
    val sortOrder: Int = 0,
    val isActive: Boolean = true,
    val createdAt: String,
    val isSynced: Boolean = false
)
