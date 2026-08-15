package com.nebulatech.lumi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String? = null,
    val supabaseUid: String? = null,
    val isPremium: Boolean = false,
    val memberSince: String,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false
)
