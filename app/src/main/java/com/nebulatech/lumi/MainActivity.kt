package com.nebulatech.lumi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nebulatech.lumi.onboarding.OnboardingRoot
import com.nebulatech.lumi.onboarding.OnboardingRoute
import com.nebulatech.lumi.ui.theme.LumiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LumiTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = com.nebulatech.lumi.home.HomeRoute
                    ) {
                        composable<OnboardingRoute> {
                            OnboardingRoot(
                                onNavigateBack = {
                                    finish()
                                },
                                onNavigateNext = { goal ->
                                    navController.navigate(com.nebulatech.lumi.home.HomeRoute)
                                }
                            )
                        }
                        composable<com.nebulatech.lumi.home.HomeRoute> {
                            com.nebulatech.lumi.home.HomeScreenContainer()
                        }
                    }
                }
            }
        }
    }
}