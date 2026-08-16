package com.nebulatech.lumi

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nebulatech.lumi.home.HomeRoute
import com.nebulatech.lumi.home.HomeScreenContainer
import com.nebulatech.lumi.onboarding.OnboardingRoot
import com.nebulatech.lumi.onboarding.OnboardingRoute
import com.nebulatech.lumi.security.BiometricAuthManager
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.LumiTheme
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary
import org.koin.androidx.compose.koinViewModel
import androidx.activity.SystemBarStyle

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            LumiTheme {
                val appVm: AppViewModel = koinViewModel()
                val isExistingUser by appVm.isExistingUser.collectAsStateWithLifecycle()

                var isUnlocked by remember {
                    mutableStateOf(!BiometricAuthManager.isBiometricEnabled(this))
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { _ ->
                    // Notification permission granted/denied
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }

                    if (BiometricAuthManager.isBiometricEnabled(this@MainActivity) && !isUnlocked) {
                        BiometricAuthManager.showBiometricPrompt(
                            activity = this@MainActivity,
                            title = "Unlock Lumi",
                            subtitle = "Verify your biometric identity to access cycle records",
                            onSuccess = { isUnlocked = true }
                        )
                    }
                }

                // Hold back NavHost until Room answers (usually < 50ms)
                if (isExistingUser == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFBF9F7))
                    )
                    return@LumiTheme
                }

                if (!isUnlocked) {
                    // Biometric Lock Screen Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFBF9F7))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF7E6EE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Lumi is Locked",
                                fontFamily = LiterataFontFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Biometric authentication is required to access your cycle records.",
                                fontFamily = ManropeFontFamily,
                                fontSize = 14.sp,
                                color = Color(0xFF6E5E67)
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Button(
                                onClick = {
                                    BiometricAuthManager.showBiometricPrompt(
                                        activity = this@MainActivity,
                                        title = "Unlock Lumi",
                                        subtitle = "Verify your biometric identity to access cycle records",
                                        onSuccess = { isUnlocked = true }
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Fingerprint,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "Unlock with Biometrics",
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    return@LumiTheme
                }

                val navController = rememberNavController()
                val startDestination: Any =
                    if (isExistingUser == true) HomeRoute else OnboardingRoute

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
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