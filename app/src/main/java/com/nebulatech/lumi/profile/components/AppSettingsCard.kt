package com.nebulatech.lumi.profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

@Composable
fun AppSettingsCard(
    notificationsEnabled: Boolean = true,
    notifDailyLog: Boolean = true,
    notifMorningBbt: Boolean = true,
    notifPeriodAlerts: Boolean = true,
    notifFertilityAlerts: Boolean = true,
    notifPhaseInsights: Boolean = true,
    onNotificationsToggle: (Boolean) -> Unit = {},
    onGranularToggle: (type: String, enabled: Boolean) -> Unit = { _, _ -> },
    onPrivacyClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showIntegrationsDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "App Settings",
                fontFamily = LiterataFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Master Notifications Item with Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = Color(0xFF4A3A43),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Notifications",
                            fontFamily = ManropeFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF26181F)
                        )
                        Text(
                            text = if (notificationsEnabled) "All reminders active" else "All reminders paused",
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF8A7A83)
                        )
                    }
                }

                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = onNotificationsToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Primary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE4DCDD)
                    )
                )
            }

            // Expandable Granular Reminders Sub-section
            AnimatedVisibility(
                visible = notificationsEnabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFFBF8F9))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Sub-item 1: Daily Log Reflection
                    NotificationSubToggleRow(
                        title = "Daily Log Reflection",
                        timeBadge = "8:30 PM",
                        isChecked = notifDailyLog,
                        onCheckedChange = { onGranularToggle("DAILY_LOG", it) }
                    )

                    HorizontalDivider(color = Color(0xFFF0E8EC), thickness = 1.dp)

                    // Sub-item 2: Morning BBT Reminder
                    NotificationSubToggleRow(
                        title = "Morning BBT Reminder",
                        timeBadge = "7:00 AM",
                        isChecked = notifMorningBbt,
                        onCheckedChange = { onGranularToggle("BBT_REMINDER", it) }
                    )

                    HorizontalDivider(color = Color(0xFFF0E8EC), thickness = 1.dp)

                    // Sub-item 3: Period Predictions & 2-Day Alerts
                    NotificationSubToggleRow(
                        title = "Period Predictions & 2-Day Alerts",
                        isChecked = notifPeriodAlerts,
                        onCheckedChange = { onGranularToggle("PERIOD_START", it) }
                    )

                    HorizontalDivider(color = Color(0xFFF0E8EC), thickness = 1.dp)

                    // Sub-item 4: Fertile Window & Ovulation Alerts
                    NotificationSubToggleRow(
                        title = "Fertile Window & Ovulation Alerts",
                        isChecked = notifFertilityAlerts,
                        onCheckedChange = { onGranularToggle("FERTILE_WINDOW", it) }
                    )

                    HorizontalDivider(color = Color(0xFFF0E8EC), thickness = 1.dp)

                    // Sub-item 5: Phase Shift Insights
                    NotificationSubToggleRow(
                        title = "Phase Shift & Wellness Insights",
                        isChecked = notifPhaseInsights,
                        onCheckedChange = { onGranularToggle("PHASE_INSIGHT", it) }
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = Color(0xFFF2ECEF)
            )

            // 2. Data Privacy & Export Item
            SettingsRowItem(
                icon = Icons.Outlined.Security,
                title = "Data Privacy & Export",
                onClick = onPrivacyClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFF2ECEF)
            )

            // 3. App Integrations Item (Marked as Available Soon)
            SettingsRowItem(
                icon = Icons.Outlined.HealthAndSafety,
                title = "App Integrations",
                subtitle = "Apple Health, Health Connect & Oura Ring",
                badgeText = "Available Soon",
                onClick = { showIntegrationsDialog = true }
            )
        }
    }

    // "Available Soon" Dialog
    if (showIntegrationsDialog) {
        AlertDialog(
            onDismissRequest = { showIntegrationsDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF9E4EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Integrations Available Soon",
                    fontFamily = LiterataFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            },
            text = {
                Text(
                    text = "We are currently building seamless synchronization with Apple Health, Health Connect, Oura Ring, and Garmin to automatically sync your BBT and sleep data.",
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    color = Color(0xFF5E4E57)
                )
            },
            confirmButton = {
                Button(
                    onClick = { showIntegrationsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Got it",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun NotificationSubToggleRow(
    title: String,
    timeBadge: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF26181F)
            )

            if (timeBadge != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF0E5EB))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = timeBadge,
                        fontFamily = ManropeFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(scale = 0.8f, defaultSize = 36.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE4DCDD)
            )
        )
    }
}

@Composable
private fun Modifier.size(scale: Float, defaultSize: androidx.compose.ui.unit.Dp): Modifier {
    return this.height(defaultSize)
}

@Composable
fun SupportAndLogoutCard(
    onHelpClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Help Center Item
            SettingsRowItem(
                icon = Icons.Outlined.Info,
                title = "Help Center",
                onClick = onHelpClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFF2ECEF)
            )

            // Terms of Service Item
            SettingsRowItem(
                icon = Icons.Outlined.Description,
                title = "Terms of Service",
                onClick = onTermsClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Log Out Button
            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                border = androidx.compose.foundation.BorderStroke(1.dp, Primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log Out",
                        fontFamily = ManropeFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF4A3A43),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontFamily = ManropeFontFamily,
                        fontSize = 15.sp,
                        color = Color(0xFF26181F)
                    )
                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF9E4EB))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontFamily = ManropeFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                    }
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF8A7B84)
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF8A7B84),
            modifier = Modifier.size(20.dp)
        )
    }
}
