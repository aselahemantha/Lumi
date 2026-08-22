package com.nebulatech.lumi.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nebulatech.lumi.analytics.AnalyticsConstants
import com.nebulatech.lumi.analytics.LocalAnalyticsTracker
import com.nebulatech.lumi.analytics.TrackScreenView
import com.nebulatech.lumi.core.ObserveAsEvents
import com.nebulatech.lumi.onboarding.components.OnboardingBottomBar
import com.nebulatech.lumi.onboarding.components.OnboardingProgressBar
import com.nebulatech.lumi.onboarding.components.OnboardingTopBar
import com.nebulatech.lumi.onboarding.coredata.CoreDataScreen
import com.nebulatech.lumi.onboarding.goalselection.GoalSelectionScreen
import com.nebulatech.lumi.onboarding.healthprofile.HealthProfileScreen
import com.nebulatech.lumi.onboarding.welcome.WelcomeScreen
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
    val tracker = LocalAnalyticsTracker.current
    val stepScreenName = "onboarding_${state.currentStep.name.lowercase()}"
    TrackScreenView(stepScreenName)

    Scaffold(
        topBar = {
            OnboardingTopBar(
                onBackClick = {
                    tracker.trackButtonClick(AnalyticsConstants.Buttons.ONBOARDING_BACK, stepScreenName)
                    onAction(OnboardingAction.ClickBack)
                }
            )
        },
        bottomBar = {
            OnboardingBottomBar(
                onBackClick = {
                    tracker.trackButtonClick(AnalyticsConstants.Buttons.ONBOARDING_BACK, stepScreenName)
                    onAction(OnboardingAction.ClickBack)
                },
                onContinueClick = {
                    tracker.trackButtonClick(
                        buttonName = AnalyticsConstants.Buttons.ONBOARDING_NEXT,
                        screenName = stepScreenName,
                        extraParams = mapOf("step" to state.currentStep.name)
                    )
                    onAction(OnboardingAction.ClickContinue)
                },
                isContinueEnabled = when (state.currentStep) {
                    OnboardingStep.WELCOME -> state.name.isNotBlank()
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
                    WelcomeScreen(
                        name = state.name,
                        onNameChange = { onAction(OnboardingAction.UpdateName(it)) }
                    )
                }
                OnboardingStep.SELECT_GOAL -> {
                    GoalSelectionScreen(
                        state = state,
                        onAction = onAction
                    )
                }
                OnboardingStep.CORE_DATA -> {
                    CoreDataScreen(
                        state = state,
                        onAction = onAction
                    )
                }
                OnboardingStep.HEALTH_PROFILE -> {
                    HealthProfileScreen(
                        state = state,
                        onAction = onAction
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
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
