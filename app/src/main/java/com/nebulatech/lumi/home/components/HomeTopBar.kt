package com.nebulatech.lumi.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.Primary

/**
 * Standard Lumi Top Bar used across Home, Insights, and Calendar screens.
 * Features "Welcome, {Name}" on left and personalized avatar button on right.
 */
@Composable
fun StandardLumiTopBar(
    modifier: Modifier = Modifier,
    userName: String? = null,
    showNotificationBell: Boolean = false,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val titleText = if (!userName.isNullOrBlank()) "Welcome, $userName" else "Welcome"
        Text(
            text = titleText,
            fontFamily = LiterataFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (showNotificationBell) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDF0F4))
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Personalized Profile Avatar Pill
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF7DDE6))
                    .border(1.5.dp, Primary.copy(alpha = 0.25f), CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                val initial = userName?.trim()?.firstOrNull()?.uppercase()
                if (!initial.isNullOrBlank()) {
                    Text(
                        text = initial,
                        fontFamily = LiterataFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    userName: String? = null,
    showNotificationBell: Boolean = true,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    StandardLumiTopBar(
        modifier = modifier,
        userName = userName,
        showNotificationBell = showNotificationBell,
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick
    )
}
