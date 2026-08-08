package com.nebulatech.lumi.onboarding.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.R

@Composable
fun OnboardingHeader(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.lumi_title),
    subtitle: String = stringResource(R.string.lumi_subtitle),
    titleStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.displayLarge,
    subtitleStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 24.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.Medium
    ),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    textAlign: TextAlign = TextAlign.Center,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = title,
            style = titleStyle,
            color = MaterialTheme.colorScheme.primary,
            textAlign = textAlign
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = subtitleStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = textAlign
        )
    }
}
