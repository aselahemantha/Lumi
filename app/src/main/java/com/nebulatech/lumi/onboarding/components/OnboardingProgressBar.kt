package com.nebulatech.lumi.onboarding.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.onboarding.OnboardingStep

@Composable
fun OnboardingProgressBar(
    currentStep: OnboardingStep,
    modifier: Modifier = Modifier,
    totalSteps: Int = 3
) {
    val step = when (currentStep) {
        OnboardingStep.WELCOME -> 0
        OnboardingStep.SELECT_GOAL -> 1
        OnboardingStep.CORE_DATA -> 2
        OnboardingStep.HEALTH_PROFILE -> 3
    }

    if (step == 0) return // Don't show progress bar on Welcome/Intro screen

    val animatedProgress by animateFloatAsState(
        targetValue = (step.toFloat() / totalSteps).coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "OnboardingStepProgressAnimation"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(100)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
        Text(
            text = "Step $step of $totalSteps",
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
