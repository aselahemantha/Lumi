package com.nebulatech.lumi.calendar.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nebulatech.lumi.ui.components.shimmerEffect

@Composable
fun CalendarScreenSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month Header Shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(34.dp)
                    .shimmerEffect(RoundedCornerShape(8.dp))
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(36.dp).shimmerEffect(CircleShape))
                Box(modifier = Modifier.size(36.dp).shimmerEffect(CircleShape))
            }
        }

        // Cycle Status Banner Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .shimmerEffect(RoundedCornerShape(20.dp))
        )

        // Month Calendar Grid Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
                .shimmerEffect(RoundedCornerShape(24.dp))
        )

        // Legend Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shimmerEffect(RoundedCornerShape(16.dp))
        )
    }
}
