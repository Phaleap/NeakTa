package com.example.neakta.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.neakta.data.SessionManager
import com.example.neakta.ui.add.AddGemScreen
import com.example.neakta.ui.auth.AuthViewModel
import com.example.neakta.ui.auth.LoginScreen
import com.example.neakta.ui.auth.RegisterScreen
import com.example.neakta.ui.components.NeaktaBottomNav
import com.example.neakta.ui.components.NavTab
import com.example.neakta.ui.detail.PinDetailScreen
import com.example.neakta.ui.home.HomeScreen
import com.example.neakta.ui.home.PinCard
import com.example.neakta.ui.home.PinViewModel
import com.example.neakta.ui.home.PinsState
import com.example.neakta.ui.map.MapScreen
import com.example.neakta.ui.onboarding.OnboardingScreen
import com.example.neakta.ui.profile.ProfileScreen
import com.example.neakta.ui.ranks.RanksScreen
import com.example.neakta.ui.settings.SettingsScreen
import com.example.neakta.ui.splash.SplashScreen

@Composable
fun NeaktaNavGraph() {
    val context = LocalContext.current
    val session = SessionManager(context)
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(session)
    )

    // ✅ Single shared PinViewModel — all screens use this one instance
    val pinViewModel: PinViewModel = viewModel(
        factory = PinViewModel.Factory(session)
    )
    val pinsState by pinViewModel.pinsState.collectAsState()

    val startDestination = when {
        session.isLoggedIn() -> "home"
        session.hasSeenOnboarding() -> "login"
        else -> "splash"
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val navBarRoutes = setOf("home", "map", "ranks", "profile")

    Scaffold(
        bottomBar = {
            if (currentRoute in navBarRoutes) {
                NeaktaBottomNav(
                    activeTab = when (currentRoute) {
                        "home"    -> NavTab.HOME
                        "map"     -> NavTab.MAP
                        "ranks"   -> NavTab.RANKS
                        "profile" -> NavTab.PROFILE
                        else      -> NavTab.HOME
                    },
                    onNavigateHome    = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                    onNavigateMap     = { navController.navigate("map") },
                    onNavigateAdd     = { navController.navigate("add_gem") },
                    onNavigateRanks   = { navController.navigate("ranks") },
                    onNavigateProfile = { navController.navigate("profile") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(paddingValues)
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
                        pinViewModel.fetchPins()
                        navController.navigate("onboarding") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate("register") },
                    viewModel = authViewModel
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        pinViewModel.fetchPins()
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() },
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
                    onNavigateToProfile = { navController.navigate("profile") },
                    onPinClick          = { pin -> navController.navigate("pin_detail/${pin.id}") },
                    viewModel           = pinViewModel   // ✅ pass shared VM
                )
            }

            composable("map") {
                MapScreen(
                    onPinClick = { pin -> navController.navigate("pin_detail/${pin.id}") },
                    viewModel  = pinViewModel             // ✅ pass shared VM
                )
            }

            composable("ranks") {
                RanksScreen(
                    onNavigateToProfile = { navController.navigate("profile") },
                    onPinClick          = { pin -> navController.navigate("pin_detail/${pin.id}") },
                    viewModel           = pinViewModel    // ✅ pass shared VM
                )
            }

            composable("profile") {
                ProfileScreen(
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        session.clearSession()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("pin_detail/{pinId}") { backStackEntry ->
                val pinId = backStackEntry.arguments?.getString("pinId")

                // ✅ Reuse already-loaded state — no extra API call
                val pin = when (val state = pinsState) {
                    is PinsState.Success -> state.pins.firstOrNull { it.id == pinId }?.let { p ->
                        PinCard(
                            id       = p.id,
                            title    = p.title,
                            province = p.provinceName ?: "",
                            category = p.categoryName ?: "",
                            votes    = p.upvoteCount,
                            story    = p.story,
                            imageUrl = p.imageUrl ?: "",
                            author   = p.authorUsername ?: "",
                            timeAgo  = p.createdAt?.take(10) ?: "",
                            lat      = p.lat?.toDouble() ?: 11.5564,
                            lng      = p.lng?.toDouble() ?: 104.9282
                        )
                    }
                    else -> null
                }

                if (pin != null) {
                    PinDetailScreen(
                        pin    = pin,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable("add_gem") {
                AddGemScreen(
                    onBack = { navController.popBackStack() },
                    onPublished = {
                        pinViewModel.fetchPins()          // ✅ explicit refresh
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}