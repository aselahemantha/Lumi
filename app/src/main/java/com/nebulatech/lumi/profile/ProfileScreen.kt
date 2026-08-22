package com.nebulatech.lumi.profile

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nebulatech.lumi.analytics.AnalyticsConstants
import com.nebulatech.lumi.analytics.LocalAnalyticsTracker
import com.nebulatech.lumi.analytics.TrackScreenView
import com.nebulatech.lumi.core.ObserveAsEvents
import com.nebulatech.lumi.data.model.PrimaryGoal
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.profile.components.AppSettingsCard
import com.nebulatech.lumi.profile.components.DataPrivacyBottomSheet
import com.nebulatech.lumi.profile.components.EditHealthProfileBottomSheet
import com.nebulatech.lumi.profile.components.HealthProfileCard
import com.nebulatech.lumi.profile.components.HelpCenterBottomSheet
import com.nebulatech.lumi.profile.components.HeroUserCard
import com.nebulatech.lumi.profile.components.SupportAndLogoutCard
import com.nebulatech.lumi.profile.components.TermsOfServiceBottomSheet
import com.nebulatech.lumi.security.BiometricAuthManager
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.LumiTheme
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileTopBar(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
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
                tint = Color(0xFF4A3A43),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onTabSelected: (HomeTab) -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val profileVm: ProfileViewModel = koinViewModel()
    val state by profileVm.state.collectAsStateWithLifecycle()
    val tracker = LocalAnalyticsTracker.current

    TrackScreenView(AnalyticsConstants.Screens.PROFILE)

    var showEditSheet by remember { mutableStateOf(false) }
    var showPrivacySheet by remember { mutableStateOf(false) }
    var showHelpSheet by remember { mutableStateOf(false) }
    var showTermsSheet by remember { mutableStateOf(false) }
    var showContactDevSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPremiumComingSoonDialog by remember { mutableStateOf(false) }

    ObserveAsEvents(profileVm.events) { event ->
        when (event) {
            ProfileEvent.NavigateToSignIn -> Unit
            ProfileEvent.LoggedOutAndAppClosed -> {
                (context as? Activity)?.finishAffinity()
            }
        }
    }

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
        if (state.isLoading) {
            com.nebulatech.lumi.profile.components.ProfileScreenSkeleton(
                modifier = Modifier.padding(innerPadding)
            )
        } else {
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
                    memberStatus = if (state.isPremium) "Premium Member" else "Free Member",
                    onMembershipClick = {
                        tracker.trackButtonClick(
                            buttonName = "btn_membership_badge",
                            screenName = AnalyticsConstants.Screens.PROFILE
                        )
                        showPremiumComingSoonDialog = true
                    }
                )

                // 2. Health Profile Card (Editable)
                HealthProfileCard(
                    cycleLengthDays = state.cycleLength,
                    periodDurationDays = state.periodDuration,
                    primaryGoal = state.primaryGoal.toDisplayName(),
                    onEditClick = {
                        tracker.trackButtonClick("btn_edit_health_profile", AnalyticsConstants.Screens.PROFILE)
                        showEditSheet = true
                    }
                )

                // 3. App Settings Card (with expanding reminder manager & privacy sheet)
                AppSettingsCard(
                    notificationsEnabled = state.notificationsEnabled,
                    notifDailyLog = state.notifDailyLog,
                    notifMorningBbt = state.notifMorningBbt,
                    notifPeriodAlerts = state.notifPeriodAlerts,
                    notifFertilityAlerts = state.notifFertilityAlerts,
                    notifPhaseInsights = state.notifPhaseInsights,
                    onNotificationsToggle = { enabled ->
                        tracker.trackButtonClick(
                            buttonName = AnalyticsConstants.Buttons.TOGGLE_NOTIFICATIONS,
                            screenName = AnalyticsConstants.Screens.PROFILE,
                            extraParams = mapOf("enabled" to enabled)
                        )
                        profileVm.onAction(ProfileAction.UpdateNotifications(enabled))
                    },
                    onGranularToggle = { type, enabled ->
                        tracker.trackButtonClick(
                            buttonName = "btn_toggle_granular_notif_${type.lowercase()}",
                            screenName = AnalyticsConstants.Screens.PROFILE,
                            extraParams = mapOf("type" to type, "enabled" to enabled)
                        )
                        profileVm.onAction(ProfileAction.ToggleNotificationSetting(type, enabled))
                    },
                    onPrivacyClick = {
                        tracker.trackButtonClick("btn_privacy_settings", AnalyticsConstants.Screens.PROFILE)
                        showPrivacySheet = true
                    }
                )

                // 4. Support & Log Out Card
                SupportAndLogoutCard(
                    onHelpClick = {
                        tracker.trackButtonClick("btn_help_center", AnalyticsConstants.Screens.PROFILE)
                        showHelpSheet = true
                    },
                    onTermsClick = {
                        tracker.trackButtonClick("btn_terms_of_service", AnalyticsConstants.Screens.PROFILE)
                        showTermsSheet = true
                    },
                    onContactDevClick = {
                        tracker.trackButtonClick("btn_contact_dev", AnalyticsConstants.Screens.PROFILE)
                        showContactDevSheet = true
                    },
                    onLogoutClick = {
                        tracker.trackButtonClick(AnalyticsConstants.Buttons.LOGOUT, AnalyticsConstants.Screens.PROFILE)
                        showLogoutDialog = true
                    }
                )

                // 5. Developer Support Lottie & Precision Tag Footer
                com.nebulatech.lumi.profile.components.DeveloperSupportFooter()

                Spacer(modifier = Modifier.height(16.dp))
            }
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

        // Data Privacy & Export Bottom Sheet Modal
        if (showPrivacySheet) {
            DataPrivacyBottomSheet(
                onDismissRequest = { showPrivacySheet = false },
                onDeleteAllData = {
                    BiometricAuthManager.setBiometricEnabled(context, false)
                    profileVm.onAction(ProfileAction.LogoutAndClearData)
                }
            )
        }

        // Help Center Bottom Sheet Modal
        if (showHelpSheet) {
            HelpCenterBottomSheet(
                onDismissRequest = { showHelpSheet = false }
            )
        }

        // Terms of Service Bottom Sheet Modal
        if (showTermsSheet) {
            TermsOfServiceBottomSheet(
                onDismissRequest = { showTermsSheet = false }
            )
        }

        // Contact Developer Bottom Sheet Modal
        if (showContactDevSheet) {
            com.nebulatech.lumi.profile.components.ContactDeveloperBottomSheet(
                onDismissRequest = { showContactDevSheet = false }
            )
        }

        // Log Out Confirmation Custom Modal Dialog
        if (showLogoutDialog) {
            Dialog(
                onDismissRequest = { showLogoutDialog = false }
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top Icon Badge
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFDE8EF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Title
                        Text(
                            text = "Log Out of Lumi?",
                            fontFamily = LiterataFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Message
                        Text(
                            text = "Logging out will clear all local cycle records, logs, and preferences from this device and close the application.",
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = Color(0xFF6E5E67),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { showLogoutDialog = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFFD4C2C8)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5E4E57))
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = {
                                    showLogoutDialog = false
                                    BiometricAuthManager.setBiometricEnabled(context, false)
                                    profileVm.onAction(ProfileAction.LogoutAndClearData)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Text(
                                    text = "Log Out",
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Premium Membership Coming Soon Dialog
        if (showPremiumComingSoonDialog) {
            Dialog(onDismissRequest = { showPremiumComingSoonDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top Sparkle Icon Badge
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFDF0F4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Title
                        Text(
                            text = "Lumi Premium",
                            fontFamily = LiterataFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF261820),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // "Coming Soon" pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Primary.copy(alpha = 0.12f))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "COMING SOON",
                                fontFamily = ManropeFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Subtitle
                        Text(
                            text = "Premium membership is coming soon with more advanced AI features to elevate your cycle insights.",
                            fontFamily = ManropeFontFamily,
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp,
                            color = Color(0xFF5E4E57),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Feature Highlights
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFAF6F8))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val premiumFeatures = listOf(
                                "Advanced AI cycle & fertility forecasting",
                                "Personalized hormone & nutrition insights",
                                "Deep symptom trend & pattern analysis",
                                "Exportable cycle health summary for doctors"
                            )
                            premiumFeatures.forEach { feature ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AutoAwesome,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = feature,
                                        fontFamily = ManropeFontFamily,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF3B2D35)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        Button(
                            onClick = { showPremiumComingSoonDialog = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text(
                                text = "Got It",
                                fontFamily = ManropeFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
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
