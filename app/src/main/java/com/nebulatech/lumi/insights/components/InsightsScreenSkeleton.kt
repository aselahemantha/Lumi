package com.nebulatech.lumi.insights.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header Title Shimmer
        Column(modifier = Modifier.padding(top = 4.dp)) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(34.dp)
                    .shimmerEffect(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(18.dp)
                    .shimmerEffect(RoundedCornerShape(6.dp))
            )
        }

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
            Box(modifier = Modifier.weight(1f).height(90.dp).shimmerEffect(RoundedCornerShape(18.dp)))
            Box(modifier = Modifier.weight(1f).height(90.dp).shimmerEffect(RoundedCornerShape(18.dp)))
            Box(modifier = Modifier.weight(1f).height(90.dp).shimmerEffect(RoundedCornerShape(18.dp)))
        }

        // Hormone Trend Interactive Chart Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .shimmerEffect(RoundedCornerShape(24.dp))
        )
    }
}
