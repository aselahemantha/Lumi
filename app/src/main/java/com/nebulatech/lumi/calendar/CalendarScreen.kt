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
import com.nebulatech.lumi.calendar.components.CalendarLegend
import com.nebulatech.lumi.calendar.components.CycleStatusBannerCard
import com.nebulatech.lumi.calendar.components.MonthlyCalendarCard
import com.nebulatech.lumi.calendar.components.PhaseDetailCard
import com.nebulatech.lumi.home.components.HomeTab
import com.nebulatech.lumi.home.components.LumiBottomNavigationBar
import com.nebulatech.lumi.home.components.StandardLumiTopBar
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.LumiTheme
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarTopBar(
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    StandardLumiTopBar(
        onProfileClick = onProfileClick,
        modifier = modifier
    )
}

@Composable
fun CalendarScreen(
    initialYearMonth: YearMonth = YearMonth.of(2023, 10),
    onTabSelected: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentYearMonth by remember { mutableStateOf(initialYearMonth) }

    val formattedMonthYear = remember(currentYearMonth) {
        currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CalendarTopBar(
                onProfileClick = { onTabSelected(HomeTab.PROFILE) }
            )
        },
        bottomBar = {
            LumiBottomNavigationBar(
                selectedTab = HomeTab.CALENDAR,
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
            // Month Header Row with Interactive Navigation Arrows (< and >)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
                        onClick = { currentYearMonth = currentYearMonth.minusMonths(1) }
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
                        onClick = { currentYearMonth = currentYearMonth.plusMonths(1) }
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

            // 1. Monthly Calendar Grid Card (Dynamically re-renders on month change)
            MonthlyCalendarCard(
                yearMonth = currentYearMonth
            )

            // 2. Calendar Legend
            CalendarLegend()

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Phase Detail Info Card
            PhaseDetailCard(
                phaseName = "Follicular Phase",
                dayNumber = 8,
                description = "Estrogen levels are rising. You might feel an increase in energy and focus today.",
                daysUntilNextPeriod = 21
            )

            // 4. Cycle Status Banner Card
            CycleStatusBannerCard(
                text = "Your cycle length is steady at 28 days."
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    LumiTheme {
        CalendarScreen()
    }
}
