package com.nebulatech.lumi.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
 * Features "Welcome, [Name]" (or "Lumi") text on left and Profile avatar button on right.
 */
@Composable
fun StandardLumiTopBar(
    userName: String? = null,
    onProfileClick: () -> Unit = {},
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
        val titleText = if (!userName.isNullOrBlank()) "Welcome, $userName" else "Lumi"
        Text(
            text = titleText,
            fontFamily = LiterataFontFamily,
            fontSize = if (!userName.isNullOrBlank()) 22.sp else 28.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )

        IconButton(onClick = onProfileClick) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFE8EB))
                    .border(1.dp, Primary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Profile",
                    tint = Color(0xFF4A3A43),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun HomeTopBar(
    userName: String? = null,
    showNotificationBell: Boolean = false,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    StandardLumiTopBar(
        userName = userName,
        onProfileClick = onProfileClick,
        modifier = modifier
    )
}
