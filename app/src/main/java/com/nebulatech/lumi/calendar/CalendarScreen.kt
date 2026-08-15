package com.nebulatech.lumi.calendar

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nebulatech.lumi.calendar.components.CalendarLegend
import com.nebulatech.lumi.calendar.components.CycleStatusBannerCard
import com.nebulatech.lumi.calendar.components.MonthlyCalendarCard
import com.nebulatech.lumi.calendar.components.PhaseDetailCard
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.LumiTheme
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    onTabSelected: (HomeTab) -> Unit = {}
) {
    val calendarVm: CalendarViewModel = koinViewModel()
    val state by calendarVm.state.collectAsStateWithLifecycle()

    val formattedMonthYear = remember(state.selectedYearMonth) {
        state.selectedYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            LumiBottomNavigationBar(
                selectedTab = HomeTab.CALENDAR,
                onTabSelected = onTabSelected
            )
        },
        containerColor = Color(0xFFFBF9F7)
    ) { innerPadding ->
        if (state.isLoading) {
            com.nebulatech.lumi.calendar.components.CalendarScreenSkeleton(
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Month Header Row with Interactive Navigation Arrows (< and >)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedMonthYear,
                    fontFamily = LiterataFontFamily,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF26181F)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            calendarVm.onAction(CalendarAction.ChangeMonth(state.selectedYearMonth.minusMonths(1)))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                            contentDescription = "Previous Month",
                            tint = Color(0xFF3B2D34),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = {
                            calendarVm.onAction(CalendarAction.ChangeMonth(state.selectedYearMonth.plusMonths(1)))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = "Next Month",
                            tint = Color(0xFF3B2D34),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // 1. Monthly Calendar Grid Card (Dynamically populated from Room)
            MonthlyCalendarCard(
                yearMonth = state.selectedYearMonth,
                cells = state.gridCells
            )

            // 2. Calendar Legend
            CalendarLegend()

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Phase Detail Info Card
            PhaseDetailCard(
                phaseName = state.currentPhase.toDisplayName(),
                dayNumber = state.cycleDay,
                description = state.phaseDescription,
                daysUntilNextPeriod = state.daysUntilNextPeriod
            )

            // 4. Cycle Status Banner Card (View All navigates to Insights)
            CycleStatusBannerCard(
                text = "Your average cycle length is steady at ${state.cycleLength} days.",
                onViewAllClick = { onTabSelected(HomeTab.INSIGHTS) }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
        }
    }
}

private fun CyclePhase.toDisplayName(): String = when (this) {
    CyclePhase.MENSTRUATION -> "Menstrual Phase"
    CyclePhase.FOLLICULAR -> "Follicular Phase"
    CyclePhase.FERTILE_WINDOW -> "Fertile Window"
    CyclePhase.LUTEAL -> "Luteal Phase"
    CyclePhase.LATE_LUTEAL -> "Late Luteal Phase"
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    LumiTheme {
        CalendarScreen()
    }
}
