package com.nebulatech.lumi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nebulatech.lumi.home.HomeRoute
import com.nebulatech.lumi.home.HomeScreenContainer
import com.nebulatech.lumi.onboarding.OnboardingRoot
import com.nebulatech.lumi.onboarding.OnboardingRoute
import com.nebulatech.lumi.ui.theme.LumiTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LumiTheme {
                val appVm: AppViewModel = koinViewModel()
                val isExistingUser by appVm.isExistingUser.collectAsStateWithLifecycle()

                // Hold back NavHost until Room answers (usually < 50ms)
                if (isExistingUser == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFBF9F7))
                    )
                    return@LumiTheme
                }

                val navController = rememberNavController()
                val startDestination: Any =
                    if (isExistingUser == true) HomeRoute else OnboardingRoute

                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable<OnboardingRoute> {
                            OnboardingRoot(
                                onNavigateBack = { finish() },
                                onNavigateNext = { _ ->
                                    navController.navigate(HomeRoute) {
                                        popUpTo<OnboardingRoute> { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable<HomeRoute> {
                            HomeScreenContainer()
                        }
                    }
                }
            }
        }
    }
}