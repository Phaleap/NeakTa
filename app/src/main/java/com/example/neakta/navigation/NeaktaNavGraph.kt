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
    val startDestination = if (session.isLoggedIn()) "home" else "splash"

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
                    navController.navigate("home") {
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
            // TODO: replace with real HomeScreen
            // HomeScreen(onLogout = {
            //     session.clearSession()
            //     navController.navigate("login") {
            //         popUpTo("home") { inclusive = true }
            //     }
            // })
        }
    }
}