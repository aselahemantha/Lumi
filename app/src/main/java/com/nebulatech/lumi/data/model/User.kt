package com.nebulatech.lumi.data.model

import java.time.Instant

data class User(
    val id: String,
    val name: String,
    val email: String? = null,
    val supabaseUid: String? = null,
    val isPremium: Boolean = false,
    val memberSince: String = Instant.now().toString(),
    val createdAt: String = Instant.now().toString(),
    val updatedAt: String = Instant.now().toString(),
    val isSynced: Boolean = false
)
