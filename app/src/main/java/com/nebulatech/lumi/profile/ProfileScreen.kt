package com.nebulatech.lumi.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nebulatech.lumi.data.model.PrimaryGoal
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.profile.components.AppSettingsCard
import com.nebulatech.lumi.profile.components.EditHealthProfileBottomSheet
import com.nebulatech.lumi.profile.components.HealthProfileCard
import com.nebulatech.lumi.profile.components.HeroUserCard
import com.nebulatech.lumi.profile.components.SupportAndLogoutCard
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.LumiTheme
import com.nebulatech.lumi.ui.theme.Primary
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileTopBar(
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Profile",
            fontFamily = LiterataFontFamily,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )

        IconButton(onClick = onNotificationClick) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = Color(0xFF3B2D34),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ProfileScreen(
    onTabSelected: (HomeTab) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profileVm: ProfileViewModel = koinViewModel()
    val state by profileVm.state.collectAsStateWithLifecycle()

    var showEditSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { ProfileTopBar(onNotificationClick = onNotificationClick) },
        bottomBar = {
            LumiBottomNavigationBar(
                selectedTab = HomeTab.PROFILE,
                onTabSelected = onTabSelected
            )
        },
        containerColor = Color(0xFFFBF9F7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero User Banner Card
            HeroUserCard(
                userName = state.userName.ifBlank { "Lumi User" },
                trackingDuration = state.trackingDuration.ifBlank { "Tracking with Lumi" },
                memberStatus = if (state.isPremium) "Premium Member" else "Free Member"
            )

            // 2. Health Profile Card (Editable)
            HealthProfileCard(
                cycleLengthDays = state.cycleLength,
                periodDurationDays = state.periodDuration,
                primaryGoal = state.primaryGoal.toDisplayName(),
                onEditClick = { showEditSheet = true }
            )

            // 3. App Settings Card (with expanding reminder manager)
            AppSettingsCard(
                notificationsEnabled = state.notificationsEnabled,
                notifDailyLog = state.notifDailyLog,
                notifMorningBbt = state.notifMorningBbt,
                notifPeriodAlerts = state.notifPeriodAlerts,
                notifFertilityAlerts = state.notifFertilityAlerts,
                notifPhaseInsights = state.notifPhaseInsights,
                onNotificationsToggle = { enabled ->
                    profileVm.onAction(ProfileAction.UpdateNotifications(enabled))
                },
                onGranularToggle = { type, enabled ->
                    profileVm.onAction(ProfileAction.ToggleNotificationSetting(type, enabled))
                }
            )

            // 4. Support & Log Out Card
            SupportAndLogoutCard()

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Edit Health Profile Bottom Sheet Modal
        if (showEditSheet) {
            EditHealthProfileBottomSheet(
                initialCycleLength = state.cycleLength,
                initialPeriodDuration = state.periodDuration,
                initialPrimaryGoal = state.primaryGoal.toDisplayName(),
                onDismissRequest = { showEditSheet = false },
                onSave = { newLength, newDuration, newGoal ->
                    profileVm.onAction(
                        ProfileAction.UpdateHealthProfile(
                            cycleLength = newLength,
                            periodDuration = newDuration,
                            primaryGoal = newGoal.toPrimaryGoal()
                        )
                    )
                    showEditSheet = false
                }
            )
        }
    }
}

private fun PrimaryGoal.toDisplayName(): String = when (this) {
    PrimaryGoal.TRACK_CYCLE -> "Track Cycles"
    PrimaryGoal.UNDERSTAND_SYMPTOMS -> "Understand Symptoms"
    PrimaryGoal.OPTIMIZE_FERTILITY -> "Optimize Fertility"
    PrimaryGoal.AVOID_PREGNANCY -> "Avoid Pregnancy"
}

private fun String.toPrimaryGoal(): PrimaryGoal = when (this) {
    "Track Cycles" -> PrimaryGoal.TRACK_CYCLE
    "Understand Symptoms" -> PrimaryGoal.UNDERSTAND_SYMPTOMS
    "Optimize Fertility" -> PrimaryGoal.OPTIMIZE_FERTILITY
    "Avoid Pregnancy" -> PrimaryGoal.AVOID_PREGNANCY
    else -> PrimaryGoal.TRACK_CYCLE
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    LumiTheme {
        ProfileScreen()
    }
}
