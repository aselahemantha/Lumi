package com.nebulatech.lumi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notification_settings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId", "reminderType"], unique = true)
    ]
)
data class NotificationSettingEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val reminderType: String, // PERIOD_START, FERTILE_WINDOW, OVULATION, BBT_REMINDER, DAILY_LOG, PERIOD_END
    val isEnabled: Boolean = true,
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val daysBefore: Int? = null,
    val updatedAt: String
)
