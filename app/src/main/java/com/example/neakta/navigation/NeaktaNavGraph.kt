package com.example.neakta.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.neakta.ui.auth.LoginScreen
import com.example.neakta.ui.auth.RegisterScreen
import com.example.neakta.ui.splash.SplashScreen

@Composable
fun NeaktaNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onGetStarted = {
                    navController.navigate("login")
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // TODO: navigate to home screen
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    // TODO: navigate to home screen
                },
                onNavigateToLogin = {
                    navController.popBackStack()   // goes back to login
                }
            )
        }
    }
}