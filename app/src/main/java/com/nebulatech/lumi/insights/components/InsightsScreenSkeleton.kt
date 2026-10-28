package com.nebulatech.lumi.insights.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nebulatech.lumi.ui.components.shimmerEffect

@Composable
fun InsightsScreenSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hormone & Phase Status Card Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .shimmerEffect(RoundedCornerShape(24.dp))
        )

        // 3 Cards Metric Row Shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .shimmerEffect(RoundedCornerShape(18.dp))
                )
            }
        }

        // Dark Banner Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .shimmerEffect(RoundedCornerShape(24.dp))
        )

        // Section Title Shimmer
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(20.dp)
                .shimmerEffect(RoundedCornerShape(6.dp))
        )

        // 3 Past Cycles Shimmer Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .width(105.dp)
                        .height(120.dp)
                        .shimmerEffect(RoundedCornerShape(20.dp))
                )
            }
        }
    }
}
