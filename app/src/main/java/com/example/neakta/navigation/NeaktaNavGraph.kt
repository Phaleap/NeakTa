package com.example.neakta.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.neakta.ui.saved.SavedScreen
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
import com.example.neakta.ui.profile.ProfileViewModel
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

    val pinViewModel: PinViewModel = viewModel(
        factory = PinViewModel.Factory(session)
    )
    val pinsState by pinViewModel.pinsState.collectAsState()

    // FIX: Create ONE shared ProfileViewModel here at the NavGraph level.
    // Both ProfileScreen and SettingsScreen receive this same instance,
    // so any update (name / avatar) in Settings immediately reflects in Profile.
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(session)
    )

    val startDestination = when {
        session.isLoggedIn() -> "home"
        session.hasSeenOnboarding() -> "login"
        else -> "onboarding"
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val navBarRoutes = setOf("home", "map", "ranks", "saved")

    Scaffold(
        bottomBar = {
            if (currentRoute in navBarRoutes) {
                NeaktaBottomNav(
                    activeTab = when (currentRoute) {
                        "home"  -> NavTab.HOME
                        "map"   -> NavTab.MAP
                        "ranks" -> NavTab.RANKS
                        "saved" -> NavTab.SAVED
                        else    -> NavTab.HOME
                    },
                    onNavigateHome    = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                    onNavigateMap     = { navController.navigate("map") },
                    onNavigateAdd     = { navController.navigate("add_gem") },
                    onNavigateRanks   = { navController.navigate("ranks") },
                    onNavigateProfile = { navController.navigate("saved") }
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
                        navController.navigate("home") {
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
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            composable("home") {
                HomeScreen(
                    onNavigateToProfile = { navController.navigate("profile") },
                    onPinClick          = { pin -> navController.navigate("pin_detail/${pin.id}") },
                    viewModel           = pinViewModel
                )
            }

            composable("map") {
                MapScreen(
                    onPinClick = { pin -> navController.navigate("pin_detail/${pin.id}") },
                    viewModel  = pinViewModel
                )
            }

            composable("ranks") {
                RanksScreen(
                    onNavigateToProfile = { navController.navigate("profile") },
                    onPinClick          = { pin -> navController.navigate("pin_detail/${pin.id}") },
                    viewModel           = pinViewModel
                )
            }

            composable("profile") {
                // FIX: Pass the shared profileViewModel instead of letting
                // ProfileScreen create its own isolated instance.
                ProfileScreen(
                    onNavigateToSettings = { navController.navigate("settings") },
                    viewModel            = profileViewModel
                )
            }
            composable("saved") {
                SavedScreen(
                    onPinClick = { pin -> navController.navigate("pin_detail/${pin.id}") },
                    onNavigateToProfile = { navController.navigate("profile") }
                )
            }

            composable("settings") {
                // FIX: Pass the exact same shared profileViewModel so that
                // when Settings saves a name or avatar and calls fetchProfile(),
                // the ProfileScreen StateFlow updates automatically.
                SettingsScreen(
                    onBack    = { navController.popBackStack() },
                    onLogout  = {
                        session.clearSession()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    viewModel = profileViewModel
                )
            }

            composable("pin_detail/{pinId}") { backStackEntry ->
                val pinId = backStackEntry.arguments?.getString("pinId")

                val pin = when (val state = pinsState) {
                    is PinsState.Success -> state.pins.firstOrNull { it.id == pinId }?.let { p ->
                        PinCard(
                            id              = p.id,
                            title           = p.title,
                            province        = p.provinceName ?: "",
                            category        = p.categoryName ?: "",
                            votes           = p.upvoteCount,
                            story           = p.story,
                            imageUrl        = p.imageUrl ?: "",
                            author          = p.authorUsername ?: "",
                            timeAgo         = p.createdAt?.take(10) ?: "",
                            lat             = p.lat?.toDouble() ?: 11.5564,
                            lng             = p.lng?.toDouble() ?: 104.9282,
                            tags            = p.tags ?: emptyList(),
                            mediaUrls       = p.mediaUrls ?: emptyList(),
                            localDirections = p.localDirections ?: "",
                            stillExistsPct  = p.score.coerceIn(0, 100).takeIf { it > 0 } ?: 97,
                            yearDiscovered  = p.createdAt?.take(4) ?: "2024"
                        )
                    }
                    else -> null
                }

                if (pin != null) {
                    PinDetailScreen(
                        pin       = pin,
                        onBack    = { navController.popBackStack() },
                        onRefresh = { pinViewModel.fetchPins() }
                    )
                }
            }

            composable("add_gem") {
                AddGemScreen(
                    onBack = { navController.popBackStack() },
                    onPublished = {
                        pinViewModel.fetchPins()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}