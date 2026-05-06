package com.example.neakta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.neakta.ui.auth.LoginScreen
import com.example.neakta.ui.splash.SplashScreen
import com.example.neakta.ui.theme.NeakTaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeakTaTheme {
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
                                // TODO: navigate to map/home screen later
                            },
                            onNavigateToRegister = {
                                // TODO: navigate to register screen later
                            }
                        )
                    }
                }
            }
        }
    }
}
