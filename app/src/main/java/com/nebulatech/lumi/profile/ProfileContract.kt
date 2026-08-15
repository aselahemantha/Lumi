package com.nebulatech.lumi.profile

import com.nebulatech.lumi.data.model.HealthConditionType
import com.nebulatech.lumi.data.model.PrimaryGoal
import com.nebulatech.lumi.data.model.WeightUnit

data class ProfileState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val userEmail: String? = null,
    val isPremium: Boolean = false,
    val memberSince: String = "",
    val trackingDuration: String = "",
    val cycleLength: Int = 28,
    val periodDuration: Int = 5,
    val primaryGoal: PrimaryGoal = PrimaryGoal.TRACK_CYCLE,
    val age: Int? = null,
    val weight: Double? = null,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val healthConditions: List<HealthConditionType> = emptyList(),
    val notificationsEnabled: Boolean = true,
    val notifDailyLog: Boolean = true,
    val notifMorningBbt: Boolean = true,
    val notifPeriodAlerts: Boolean = true,
    val notifFertilityAlerts: Boolean = true,
    val notifPhaseInsights: Boolean = true
)

sealed interface ProfileAction {
    data object LoadProfile : ProfileAction
    data class UpdateHealthProfile(
        val cycleLength: Int,
        val periodDuration: Int,
        val primaryGoal: PrimaryGoal
    ) : ProfileAction
    data class UpdateNotifications(val enabled: Boolean) : ProfileAction
    data class ToggleNotificationSetting(val type: String, val enabled: Boolean) : ProfileAction
}

sealed interface ProfileEvent {
    data object NavigateToSignIn : ProfileEvent
}
