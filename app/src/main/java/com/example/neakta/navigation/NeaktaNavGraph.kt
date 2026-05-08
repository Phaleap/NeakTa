package com.example.neakta.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.neakta.data.SessionManager
import com.example.neakta.ui.auth.AuthViewModel
import com.example.neakta.ui.auth.LoginScreen
import com.example.neakta.ui.auth.RegisterScreen
import com.example.neakta.ui.splash.SplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.neakta.ui.onboarding.OnboardingScreen

@Composable
fun NeaktaNavGraph() {
    val context       = LocalContext.current
    val session       = SessionManager(context)
    val navController = rememberNavController()

    // Shared AuthViewModel with SessionManager injected
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(session)
    )

    // Decide where to start:
    // - already logged in  → go straight to home (TODO)
    // - first launch       → splash (will route to onboarding later)
    val startDestination = "splash"

    NavHost(
        navController    = navController,
        startDestination = startDestination
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
                    navController.navigate("onboarding") {  // ← was "home"
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

        composable("home") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0C0A07)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "HOME — Coming Soon",
                    color = Color(0xFFD4B870)
                )
            }
        }
        // Add this new composable:
        composable("onboarding") {
            OnboardingScreen(
                onFinished = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
    }
}