package com.nebulatech.lumi.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.LumiTheme
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotificationCenterScreen(
    onNavigateBack: () -> Unit = {},
    onTabSelected: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: NotificationCenterViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Title & Subtitle (Left-aligned, consistent with Profile and Insights)
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = "Notification Center",
                    fontFamily = LiterataFontFamily,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Preview of system alerts, reminders, and phase insights designed to gently support your daily wellness journey.",
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFF5E4E57)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Notification Cards List from Room database
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                state.notifications.forEach { notification ->
                    NotificationItemCard(item = notification)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCenterScreenPreview() {
    LumiTheme {
        NotificationCenterScreen()
    }
}
