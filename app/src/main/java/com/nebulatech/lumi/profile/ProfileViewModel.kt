package com.nebulatech.lumi.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nebulatech.lumi.data.repository.RoomUserRepository
import com.nebulatech.lumi.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val userId = RoomUserRepository.DEFAULT_LOCAL_USER_ID

    val state: StateFlow<ProfileState> = combine(
        userRepository.getCurrentUser(),
        userRepository.getUserProfileFlow(userId)
    ) { user, profile ->
        if (user == null) return@combine ProfileState(isLoading = false)

        ProfileState(
            isLoading = false,
            userName = user.name,
            userEmail = user.email,
            isPremium = user.isPremium,
            memberSince = user.memberSince,
            trackingDuration = computeTrackingDuration(user.memberSince),
            cycleLength = profile?.cycleLength ?: 28,
            periodDuration = profile?.periodDuration ?: 5,
            primaryGoal = profile?.primaryGoal ?: com.nebulatech.lumi.data.model.PrimaryGoal.TRACK_CYCLE,
            age = profile?.age,
            weight = profile?.weight,
            weightUnit = profile?.weightUnit ?: com.nebulatech.lumi.data.model.WeightUnit.KG,
            healthConditions = profile?.healthConditions ?: emptyList(),
            notificationsEnabled = profile?.notificationsEnabled ?: true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileState()
    )

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.LoadProfile -> Unit // handled by init
            is ProfileAction.UpdateNotifications -> {
                viewModelScope.launch {
                    // Future: update notification settings in NotificationSettingRepository
                }
            }
        }
    }

    private fun computeTrackingDuration(memberSince: String): String {
        return try {
            val start = Instant.parse(memberSince)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val months = ChronoUnit.MONTHS.between(start, LocalDate.now()).toInt()
            when {
                months < 1 -> "Just started tracking"
                months == 1 -> "Tracking for 1 month"
                months < 12 -> "Tracking for $months months"
                months < 24 -> "Tracking for 1 year"
                else -> "Tracking for ${months / 12} years"
            }
        } catch (e: Exception) {
            "Tracking with Lumi"
        }
    }
}
