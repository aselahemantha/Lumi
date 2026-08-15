package com.nebulatech.lumi.profile.components

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
fun ProfileScreenSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar Shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(34.dp)
                    .shimmerEffect(RoundedCornerShape(8.dp))
            )
            Box(modifier = Modifier.size(36.dp).shimmerEffect(CircleShape))
        }

        // Hero User Card Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .shimmerEffect(RoundedCornerShape(28.dp))
        )

        // Health Profile Card Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .shimmerEffect(RoundedCornerShape(24.dp))
        )

        // Settings Card Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .shimmerEffect(RoundedCornerShape(24.dp))
        )
    }
}
