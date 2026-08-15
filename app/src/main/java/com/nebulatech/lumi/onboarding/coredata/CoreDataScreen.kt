package com.nebulatech.lumi.onboarding.coredata

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun CoreDataScreen(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showManualPastCyclesSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        OnboardingHeader(
            title = stringResource(R.string.core_data_title),
            subtitle = stringResource(R.string.core_data_subtitle),
            titleStyle = MaterialTheme.typography.headlineLarge,
            subtitleStyle = MaterialTheme.typography.bodyLarge,
            horizontalAlignment = Alignment.Start,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(24.dp))

        DatePickerCard(
            selectedDate = state.firstDayOfLastPeriod,
            periodDuration = state.periodDuration,
            onDateSelected = { onAction(OnboardingAction.UpdateFirstDayOfLastPeriod(it)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CounterCard(
            title = stringResource(R.string.period_duration_title),
            subtitle = stringResource(R.string.period_duration_subtitle),
            value = state.periodDuration,
            onValueChange = { onAction(OnboardingAction.UpdatePeriodDuration(it)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CounterCard(
            title = stringResource(R.string.cycle_length_title),
            subtitle = stringResource(R.string.cycle_length_subtitle),
            value = state.cycleLength,
            onValueChange = { onAction(OnboardingAction.UpdateCycleLength(it)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CalibrationInfoBox(
            hasManualData = state.customPastCycles != null,
            onEnterManuallyClick = { showManualPastCyclesSheet = true }
        )
    }

    if (showManualPastCyclesSheet) {
        ManualPastCyclesBottomSheet(
            firstDayOfLastPeriod = state.firstDayOfLastPeriod,
            defaultCycleLength = state.cycleLength,
            defaultPeriodDuration = state.periodDuration,
            initialPastCycles = state.customPastCycles,
            onDismissRequest = { showManualPastCyclesSheet = false },
            onSave = { onAction(OnboardingAction.UpdateCustomPastCycles(it)) }
        )
    }
}
