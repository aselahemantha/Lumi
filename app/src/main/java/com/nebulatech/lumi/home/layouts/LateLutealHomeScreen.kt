package com.nebulatech.lumi.home.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.HomeTopBar
import com.nebulatech.lumi.home.components.LateLutealHeaderCard
import com.nebulatech.lumi.home.components.LogSymptomsSection
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.home.components.LumiInsightPinkCard
import com.nebulatech.lumi.home.components.ThirtyDayTrendsCard
import com.nebulatech.lumi.logging.LogFlowBottomSheet
import com.nebulatech.lumi.logging.LoggingAction
import com.nebulatech.lumi.logging.LoggingEvent
import com.nebulatech.lumi.logging.LoggingViewModel
import com.nebulatech.lumi.ui.theme.LumiTheme

/**
 * Layout 1: Late Luteal / Symptom Grid Screen
 * Triggered during LATE_LUTEAL phase.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LateLutealHomeScreen(
    cycleDay: Int = 24,
    progressRatio: Float = 0.85f,
    loggingViewModel: LoggingViewModel? = null,
    onViewAllSymptoms: () -> Unit = {},
    onTabSelected: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showLogSheet by remember { mutableStateOf(false) }

    LaunchedEffect(loggingViewModel) {
        loggingViewModel?.events?.collect { event ->
            when (event) {
                is LoggingEvent.FlowSaved -> showLogSheet = false
                else -> Unit
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { HomeTopBar(showNotificationBell = false) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LateLutealHeaderCard(
                dayNumber = cycleDay,
                title = "Late Luteal Phase",
                description = "Progesterone is dropping. You may notice shifts in energy and mood.",
                progressRatio = progressRatio
            )

            LogSymptomsSection(
                onSaveClick = { showLogSheet = true },
                onViewAllClick = onViewAllSymptoms
            )

            LumiInsightPinkCard(
                insightText = "You frequently log migraines around Day $cycleDay. This is common when estrogen drops.",
                actionText = "Action: Try increasing magnesium intake today."
            )

            ThirtyDayTrendsCard(
                mostFrequentSymptom = "Headaches"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showLogSheet) {
            LogFlowBottomSheet(
                onDismissRequest = { showLogSheet = false },
                onSaveLog = { flow, symptoms, mood ->
                    loggingViewModel?.onAction(
                        LoggingAction.SaveFlowLog(
                            flow = flow,
                            mood = mood,
                            symptoms = symptoms
                        )
                    ) ?: run { showLogSheet = false }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LateLutealHomeScreenPreview() {
    LumiTheme {
        LateLutealHomeScreen(cycleDay = 24, progressRatio = 0.85f)
    }
}
