package com.nebulatech.lumi.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nebulatech.lumi.ui.components.shimmerEffect

@Composable
fun HomeScreenSkeleton(
    modifier: Modifier = Modifier,
    selectedTab: HomeTab = HomeTab.TODAY,
    onTabSelected: (HomeTab) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(32.dp)
                        .shimmerEffect(RoundedCornerShape(8.dp))
                )
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shimmerEffect(CircleShape)
                )
            }
        },
        bottomBar = {
            LumiBottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = Color(0xFFFBF9F7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Shimmer Ring Circle
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .shimmerEffect(CircleShape)
            )

            // 2. Shimmer Insight Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .shimmerEffect(RoundedCornerShape(24.dp))
            )

            // 3. Shimmer Action Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shimmerEffect(RoundedCornerShape(16.dp))
            )

            // 4. Shimmer 7 Days Strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .shimmerEffect(RoundedCornerShape(20.dp))
            )
        }
    }
}
