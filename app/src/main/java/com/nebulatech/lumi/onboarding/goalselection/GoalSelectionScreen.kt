package com.nebulatech.lumi.onboarding.goalselection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nebulatech.lumi.R
import com.nebulatech.lumi.onboarding.OnboardingAction
import com.nebulatech.lumi.onboarding.OnboardingGoal
import com.nebulatech.lumi.onboarding.OnboardingState
import com.nebulatech.lumi.onboarding.components.HeroImageCard
import com.nebulatech.lumi.onboarding.components.OnboardingHeader

@Composable
fun GoalSelectionScreen(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingHeader()
        Spacer(modifier = Modifier.height(24.dp))
        HeroImageCard()
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.primary_goal_question),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))

        GoalCard(
            title = stringResource(R.string.goal_track_cycle_title),
            subtitle = stringResource(R.string.goal_track_cycle_desc),
            icon = Icons.Default.DateRange,
            isSelected = state.selectedGoal == OnboardingGoal.TRACK_CYCLE,
            onClick = { onAction(OnboardingAction.SelectGoal(OnboardingGoal.TRACK_CYCLE)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        GoalCard(
            title = stringResource(R.string.goal_understand_symptoms_title),
            subtitle = stringResource(R.string.goal_understand_symptoms_desc),
            icon = Icons.Default.Favorite,
            isSelected = state.selectedGoal == OnboardingGoal.UNDERSTAND_SYMPTOMS,
            onClick = { onAction(OnboardingAction.SelectGoal(OnboardingGoal.UNDERSTAND_SYMPTOMS)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        GoalCard(
            title = stringResource(R.string.goal_optimize_fertility_title),
            subtitle = stringResource(R.string.goal_optimize_fertility_desc),
            icon = Icons.Default.Star,
            isSelected = state.selectedGoal == OnboardingGoal.OPTIMIZE_FERTILITY,
            onClick = { onAction(OnboardingAction.SelectGoal(OnboardingGoal.OPTIMIZE_FERTILITY)) }
        )
    }
}
