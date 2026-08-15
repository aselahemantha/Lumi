package com.nebulatech.lumi.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

enum class HomeTab(
    val label: String,
    val icon: ImageVector
) {
    TODAY("Today", Icons.Outlined.CalendarToday),
    CALENDAR("Calendar", Icons.Outlined.CalendarMonth),
    INSIGHTS("Insights", Icons.Outlined.AutoGraph),
    PROFILE("Profile", Icons.Outlined.Person)
}

@Composable
fun LumiBottomNavigationBar(
    selectedTab: HomeTab = HomeTab.TODAY,
    onTabSelected: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFFAF7F5),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 4.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val contentColor = if (isSelected) Color.White else Color(0xFF53434B)
                val backgroundColor = if (isSelected) Primary else Color.Transparent

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(backgroundColor)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = tab.label,
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = contentColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
