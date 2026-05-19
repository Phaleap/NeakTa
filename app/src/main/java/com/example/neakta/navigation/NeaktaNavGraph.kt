package com.example.neakta.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.neakta.data.SessionManager
import com.example.neakta.ui.auth.AuthViewModel
import com.example.neakta.ui.auth.LoginScreen
import com.example.neakta.ui.auth.RegisterScreen
import com.example.neakta.ui.detail.PinDetailScreen
import com.example.neakta.ui.home.HomeScreen
import com.example.neakta.ui.home.recentPins
import com.example.neakta.ui.home.trendingPins
import com.example.neakta.ui.map.MapScreen
import com.example.neakta.ui.onboarding.OnboardingScreen
import com.example.neakta.ui.profile.ProfileScreen
import com.example.neakta.ui.settings.SettingsScreen
import com.example.neakta.ui.splash.SplashScreen
import com.example.neakta.ui.add.AddGemScreen
import com.example.neakta.ui.ranks.RanksScreen   // ← NEW

@Composable
fun NeaktaNavGraph() {
    val context = LocalContext.current
    val session = SessionManager(context)
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(session)
    )

    val allPins = remember { (trendingPins + recentPins).associateBy { it.id } }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onGetStarted = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("onboarding") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                viewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onFinished = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToMap = { navController.navigate("map") },
                onNavigateToAdd = { navController.navigate("add_gem") },
                onNavigateToRanks = { navController.navigate("ranks") },
                onNavigateToProfile = { navController.navigate("profile") },
                onPinClick = { pin -> navController.navigate("pin_detail/${pin.id}") }
            )
        }

        composable("map") {
            MapScreen(
                onPinClick = { pin -> navController.navigate("pin_detail/${pin.id}") },
                onNavigateHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateAdd = { navController.navigate("add_gem") },
                onNavigateRanks = { navController.navigate("ranks") },
                onNavigateProfile = { navController.navigate("profile") }
            )
        }

        // ── NEW: Ranks screen ────────────────────────────────
        composable("ranks") {
            RanksScreen(
                onNavigateHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToMap = { navController.navigate("map") },
                onNavigateToAdd = { navController.navigate("add_gem") },
                onNavigateToProfile = { navController.navigate("profile") },
                onPinClick = { pin -> navController.navigate("pin_detail/${pin.id}") }
            )
        }

        composable("profile") {
            ProfileScreen(
                onNavigateHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToMap = { navController.navigate("map") },
                onNavigateToAdd = { navController.navigate("add_gem") },
                onNavigateToRanks = { navController.navigate("ranks") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("pin_detail/{pinId}") { backStackEntry ->
            val pinId = backStackEntry.arguments
                ?.getString("pinId")
                ?.toIntOrNull()
            val pin = pinId?.let { allPins[it] }
            if (pin != null) {
                PinDetailScreen(
                    pin = pin,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable("add_gem") {
            AddGemScreen(
                onBack = { navController.popBackStack() },
                onPublished = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
