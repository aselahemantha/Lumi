package com.nebulatech.lumi.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nebulatech.lumi.data.repository.NotificationRepository
import com.nebulatech.lumi.data.repository.RoomUserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationCenterViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val userId = RoomUserRepository.DEFAULT_LOCAL_USER_ID

    val state: StateFlow<NotificationCenterState> = notificationRepository
        .getNotificationsFlow(userId)
        .map { list ->
            NotificationCenterState(
                isLoading = false,
                notifications = list
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotificationCenterState(isLoading = false, notifications = emptyList())
        )

    fun clearAll() {
        viewModelScope.launch {
            notificationRepository.clearAll(userId)
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            notificationRepository.deleteNotification(id)
        }
    }
}
