package com.nebulatech.lumi.notifications

data class NotificationCenterState(
    val isLoading: Boolean = false,
    val notifications: List<LumiNotificationItem> = emptyList()
)
