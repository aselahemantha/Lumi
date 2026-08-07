package com.nebulatech.lumi.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nebulatech.lumi.R
import com.nebulatech.lumi.core.ObserveAsEvents
import com.nebulatech.lumi.onboarding.components.CalibrationInfoBox
import com.nebulatech.lumi.onboarding.components.CounterCard
import com.nebulatech.lumi.onboarding.components.DatePickerCard
import com.nebulatech.lumi.onboarding.components.GoalCard
import com.nebulatech.lumi.onboarding.components.HealthProfileCard
import com.nebulatech.lumi.onboarding.components.HeroImageCard
import com.nebulatech.lumi.onboarding.components.OnboardingBottomBar
import com.nebulatech.lumi.onboarding.components.OnboardingHeader
import com.nebulatech.lumi.onboarding.components.OnboardingProgressBar
import com.nebulatech.lumi.onboarding.components.OnboardingTopBar
import com.nebulatech.lumi.ui.theme.LumiTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingRoot(
    onNavigateBack: () -> Unit,
    onNavigateNext: (OnboardingGoal) -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is OnboardingEvent.NavigateBack -> onNavigateBack()
            is OnboardingEvent.NavigateNext -> onNavigateNext(event.goal)
        }
    }

    OnboardingScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit
) {
    Scaffold(
        topBar = {
            OnboardingTopBar(
                onBackClick = { onAction(OnboardingAction.ClickBack) }
            )
        },
        bottomBar = {
            OnboardingBottomBar(
                onBackClick = { onAction(OnboardingAction.ClickBack) },
                onContinueClick = { onAction(OnboardingAction.ClickContinue) },
                isContinueEnabled = when (state.currentStep) {
                    OnboardingStep.WELCOME -> true
                    OnboardingStep.SELECT_GOAL -> state.selectedGoal != null
                    OnboardingStep.CORE_DATA -> true
                    OnboardingStep.HEALTH_PROFILE -> state.age.isNotEmpty()
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingProgressBar(currentStep = state.currentStep)
            
            if (state.currentStep != OnboardingStep.WELCOME) {
                Spacer(modifier = Modifier.height(24.dp))
            }

            when (state.currentStep) {
                OnboardingStep.WELCOME -> {
                    WelcomeContent()
                }
                OnboardingStep.SELECT_GOAL -> {
                    GoalSelectionContent(
                        state = state,
                        onAction = onAction
                    )
                }
                OnboardingStep.CORE_DATA -> {
                    CoreDataContent(
                        state = state,
                        onAction = onAction
                    )
                }
                OnboardingStep.HEALTH_PROFILE -> {
                    HealthProfileContent(
                        state = state,
                        onAction = onAction
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WelcomeContent(
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
            text = stringResource(R.string.welcome_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun GoalSelectionContent(
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

@Composable
private fun CoreDataContent(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier
) {
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

        CalibrationInfoBox()
    }
}

@Composable
private fun HealthProfileContent(
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

@Preview(showBackground = true)
@Composable
private fun OnboardingWelcomeScreenPreview() {
    LumiTheme {
        OnboardingScreen(
            state = OnboardingState(currentStep = OnboardingStep.WELCOME),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingGoalScreenPreview() {
    LumiTheme {
        OnboardingScreen(
            state = OnboardingState(
                currentStep = OnboardingStep.SELECT_GOAL,
                selectedGoal = OnboardingGoal.TRACK_CYCLE
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingCoreDataScreenPreview() {
    LumiTheme {
        OnboardingScreen(
            state = OnboardingState(
                currentStep = OnboardingStep.CORE_DATA,
                selectedGoal = OnboardingGoal.TRACK_CYCLE
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingHealthProfileScreenPreview() {
    LumiTheme {
        OnboardingScreen(
            state = OnboardingState(
                currentStep = OnboardingStep.HEALTH_PROFILE,
                selectedGoal = OnboardingGoal.TRACK_CYCLE,
                age = "28",
                weight = "65",
                selectedConditions = setOf("PCOS")
            ),
            onAction = {}
        )
    }
}
