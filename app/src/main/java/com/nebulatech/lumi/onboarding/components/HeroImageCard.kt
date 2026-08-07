package com.nebulatech.lumi.onboarding.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nebulatech.lumi.R

@Composable
fun HeroImageCard(
    @DrawableRes imageResId: Int = R.drawable.onboarding_hero,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                clip = true
            ),
        contentScale = ContentScale.Crop
    )
}
