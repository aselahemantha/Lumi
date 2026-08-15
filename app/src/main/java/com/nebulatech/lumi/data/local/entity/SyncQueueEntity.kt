package com.nebulatech.lumi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sync_queue",
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
        Index(value = ["status"]),
        Index(value = ["createdAt"])
    ]
)
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val tableName: String,
    val recordId: String,
    val operation: String, // INSERT, UPDATE, DELETE
    val payload: String, // JSON serialized string
    val status: String = "PENDING", // PENDING, IN_PROGRESS, COMPLETED, FAILED
    val retryCount: Int = 0,
    val createdAt: String,
    val lastAttemptedAt: String? = null
)
