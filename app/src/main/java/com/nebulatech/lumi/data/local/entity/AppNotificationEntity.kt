package com.nebulatech.lumi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val category: String,
    val title: String,
    val body: String,
    val timeText: String,
    val isRead: Boolean = false,
    val createdAt: String
)
