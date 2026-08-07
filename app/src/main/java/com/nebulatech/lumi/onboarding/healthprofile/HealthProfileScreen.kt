package com.nebulatech.lumi.onboarding.healthprofile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nebulatech.lumi.R
import com.nebulatech.lumi.onboarding.OnboardingAction
import com.nebulatech.lumi.onboarding.OnboardingState
import com.nebulatech.lumi.onboarding.components.OnboardingHeader

@Composable
fun HealthProfileScreen(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        OnboardingHeader(
            title = stringResource(R.string.health_profile_title),
            subtitle = stringResource(R.string.health_profile_subtitle),
            titleStyle = MaterialTheme.typography.headlineLarge,
            subtitleStyle = MaterialTheme.typography.bodyLarge,
            horizontalAlignment = Alignment.Start,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(24.dp))

        HealthProfileCard(
            age = state.age,
            onAgeChange = { onAction(OnboardingAction.UpdateAge(it)) },
            weight = state.weight,
            onWeightChange = { onAction(OnboardingAction.UpdateWeight(it)) },
            weightUnit = state.weightUnit,
            onWeightUnitChange = { onAction(OnboardingAction.UpdateWeightUnit(it)) },
            selectedConditions = state.selectedConditions,
            onConditionToggle = { onAction(OnboardingAction.ToggleCondition(it)) }
        )
    }
}
