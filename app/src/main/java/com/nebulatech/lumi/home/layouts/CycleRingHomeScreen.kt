package com.nebulatech.lumi.home.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.nebulatech.lumi.home.components.CycleRingWidget
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.HomeTopBar
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.home.components.LumiInsightCard
import com.nebulatech.lumi.home.components.Next7DaysCalendarStrip
import com.nebulatech.lumi.logging.LogFlowBottomSheet
import com.nebulatech.lumi.ui.theme.LumiTheme
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

/**
 * Layout 2: Standard Cycle Ring Screen
 * Triggered during MENSTRUATION, FOLLICULAR, and LUTEAL phases.
 * Provides cycle progress awareness, period prediction, and quick logging.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CycleRingHomeScreen(
    cycleDay: Int = 24,
    subLabelText: String = "Period starts in ~4 days",
    onLogFlowClick: () -> Unit = {},
    onTabSelected: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showLogSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(showNotificationBell = true)
        },
        bottomBar = {
            LumiBottomNavigationBar(
                selectedTab = HomeTab.TODAY,
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Cycle Ring Arc
            CycleRingWidget(
                cycleDay = cycleDay,
                subLabelText = subLabelText,
                progressRatio = 0.85f
            )

            // 2. Primary Log Flow Button
            Button(
                onClick = {
                    showLogSheet = true
                    onLogFlowClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log Flow",
                        fontFamily = ManropeFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 3. Lumi Insight Card
            LumiInsightCard(
                title = "Lumi Insight",
                text = "Your last 3 cycles have varied by 8 days. To help stabilize ovulation this week, try swapping high-intensity workouts for yoga."
            )

            // 4. Next 7 Days Strip
            Next7DaysCalendarStrip()

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Log Flow Bottom Sheet Modal
        if (showLogSheet) {
            LogFlowBottomSheet(
                onDismissRequest = { showLogSheet = false },
                onSaveLog = { _, _, _ -> showLogSheet = false }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CycleRingHomeScreenPreview() {
    LumiTheme {
        CycleRingHomeScreen()
    }
}
