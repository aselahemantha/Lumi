package com.nebulatech.lumi.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.R
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.nebulatech.lumi.ui.theme.TextStrong
import com.nebulatech.lumi.ui.theme.TextSubtle

/**
 * Clean, consistent Lumi Top Bar used on the Today / Home screen.
 * Displays "Welcome, {CapitalizedName}" with today's date, and a matching
 * frameless notification bell on the right (identical to Profile & Insights).
 */
@Composable
fun StandardLumiTopBar(
    modifier: Modifier = Modifier,
    userName: String? = null,
    showNotificationBell: Boolean = true,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val formattedDate = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()))
    }

    val formattedName = remember(userName) {
        userName?.trim()?.takeIf { it.isNotEmpty() }?.let { raw ->
            raw.split(" ").joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
        }
    }

    val titleText = if (!formattedName.isNullOrBlank()) "Welcome, $formattedName" else "Welcome"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = titleText,
                fontFamily = LiterataFontFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formattedDate,
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSubtle,
            )
        }

        if (showNotificationBell) {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.notifications_desc),
                    tint = TextStrong,
                    modifier = Modifier.size(24.dp),
                )
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
    onProfileClick: () -> Unit = {},
) {
    StandardLumiTopBar(
        modifier = modifier,
        userName = userName,
        showNotificationBell = showNotificationBell,
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
    )
}
