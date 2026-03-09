package com.bridgebase.bridgebase.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.bridgebase.bridgebase.ui.goals.AddGoalScreen
import com.bridgebase.bridgebase.ui.journal.AddJournalScreen
import com.bridgebase.bridgebase.ui.goals.GoalDetailScreen
import com.bridgebase.bridgebase.ui.goals.GoalsScreen
import com.bridgebase.bridgebase.ui.home.HomeScreen
import com.bridgebase.bridgebase.ui.journal.JournalDetailScreen
import com.bridgebase.bridgebase.ui.journal.JournalScreen
import com.bridgebase.bridgebase.ui.login.LoginScreen
import com.bridgebase.bridgebase.ui.splash.SplashScreen
import com.bridgebase.bridgebase.ui.components.BottomNavBar
import com.bridgebase.bridgebase.ui.components.TopBar
import com.bridgebase.bridgebase.ui.forgot.ForgotPasswordViewModel
import com.bridgebase.bridgebase.ui.goals.GoalsViewModel
import com.bridgebase.bridgebase.ui.home.HomeViewModel
import com.bridgebase.bridgebase.ui.journal.JournalViewModel
import com.bridgebase.bridgebase.ui.login.LoginViewModel
import com.bridgebase.bridgebase.ui.settings.SettingsScreen
import com.bridgebase.bridgebase.ui.settings.SettingsViewModel
import com.bridgebase.bridgebase.ui.signup.SignupScreen
import com.bridgebase.bridgebase.ui.signup.SignupViewModel
import com.bridgebase.bridgebase.ui.splash.SplashViewModel

/**
 * Global navigation graph for the BridgeBase template.
 *
 * - Handles routing between Splash, Login, Home, and Journal features.
 * - Manages top/bottom bar visibility per screen.
 * - Demonstrates correct Hilt ViewModel scoping across nested graphs.
 *
 * This structure is intentionally simple and ideal for Upwork clients who
 * want a clean, scalable Compose + Navigation + Hilt starter architecture.
 */
@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    // Controls visibility of top + bottom bars across screens.
    // Saved so configuration changes do not reset UI state.
    val barVisibility = rememberSaveable { (mutableStateOf(false)) }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { if (barVisibility.value) TopBar() },
        bottomBar = { if (barVisibility.value) BottomNavBar(navController) }
    ) { innerPadding ->

        /**
         * Root-level NavHost
         * -------------------------------------------------------
         * All navigation flows (Splash → Login → Home → Journal)
         * are declared here.
         */
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            /** SPLASH SCREEN
             * -------------------------------------------------------
             * Decides whether to route user to Login or Home.
             */
            composable(Screen.Splash.route) {
                barVisibility.value = false
                val splashViewModel = hiltViewModel<SplashViewModel>()
                SplashScreen(
                    splashViewModel,
                    navigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) // clears back stack
                        }
                    },
                    navigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0)
                        }
                    }
                )
            }

            /** LOGIN SCREEN
             * -------------------------------------------------------
             * No top/bottom bars while authenticating.
             */
            composable(Screen.Login.route) {
                barVisibility.value = false
                val loginViewModel = hiltViewModel<LoginViewModel>()
                val forgotViewModel = hiltViewModel<ForgotPasswordViewModel>()
                LoginScreen(
                    loginViewModel,
                    forgotViewModel,
                    navigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0)
                        }
                    },
                    navigateToSignup = {
                        navController.navigate(Screen.Signup.route)
                    }
                )
            }

            /** SIGNUP SCREEN
             * -------------------------------------------------------
             * No top/bottom bars while authenticating.
             */
            composable(Screen.Signup.route) {
                barVisibility.value = false
                val signupViewModel = hiltViewModel<SignupViewModel>()
                SignupScreen(
                    signupViewModel,
                    navigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0)
                        }
                    },
                    navigateBackToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            /** HOME SCREEN
             * -------------------------------------------------------
             * Primary app destination — show navigation bars.
             */
            composable(Screen.Home.route) {
                barVisibility.value = true
                val homeViewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(
                    homeViewModel,
                    navigateToJournal = {
                        navController.navigate(Screen.Journal.route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToGoals = {
                        navController.navigate(Screen.Goals.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            /**
             * JOURNAL FEATURE GRAPH
             * -------------------------------------------------------
             * A nested graph so all journal screens share the same
             * JournalViewModel instance — crucial for add/edit/detail.
             */
            navigation(
                route = "journal_graph",
                startDestination = Screen.Journal.route
            ) {

                // --- Journal List ---
                composable(Screen.Journal.route) { backStackEntry ->
                    barVisibility.value = true

                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("journal_graph")
                    }
                    val vm = hiltViewModel<JournalViewModel>(parentEntry)

                    JournalScreen(
                        viewModel = vm,
                        navigateToJournalDetail = { id ->
                            navController.navigate(Screen.JournalDetail(id).route)
                        },
                        navigateToAddJournal = {
                            navController.navigate(Screen.AddJournal.route)
                        }
                    )
                }

                // --- Journal Detail ---
                composable(Screen.JournalDetail.ROUTE) { backStackEntry ->
                    barVisibility.value = false

                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("journal_graph")
                    }
                    val vm = hiltViewModel<JournalViewModel>(parentEntry)

                    val entryId = backStackEntry.arguments?.getString("entryId") ?: ""
                    val entry = vm.entries.value.firstOrNull { it.id == entryId }
                        ?: return@composable

                    JournalDetailScreen(
                        entry = entry,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // --- Add Journal ---
                composable(Screen.AddJournal.route) { backStackEntry ->
                    barVisibility.value = false

                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("journal_graph")
                    }
                    val vm = hiltViewModel<JournalViewModel>(parentEntry)

                    AddJournalScreen(
                        viewModel = vm,
                        onBack = { navController.popBackStack() },
                        onSaveComplete = { navController.popBackStack() }
                    )
                }

                navigation(
                    route = "goal_graph",
                    startDestination = Screen.Journal.route
                ) {

                    // --- Goals ---
                    composable(Screen.Goals.route) { backStackEntry ->
                        barVisibility.value = true

                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("goal_graph")
                        }
                        val vm = hiltViewModel<GoalsViewModel>(parentEntry)

                        GoalsScreen(
                            vm,
                            navigateToAddGoal = {
                                navController.navigate(Screen.AddGoal.route)
                            },
                            navigateToDetail = { id ->
                                navController.navigate(Screen.GoalDetail(id).route)
                            }
                        )
                    }

                    // --- Goal Detail ---
                    composable(Screen.GoalDetail.ROUTE) { backStackEntry ->
                        barVisibility.value = false

                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("goal_graph")
                        }
                        val vm = hiltViewModel<GoalsViewModel>(parentEntry)

                        val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
                        val goal = vm.allGoals.value.firstOrNull { it.id == goalId }
                            ?: return@composable

                        GoalDetailScreen(
                            goal = goal,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AddGoal.route) { backStackEntry ->
                        barVisibility.value = false

                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("goal_graph")
                        }
                        val vm = hiltViewModel<GoalsViewModel>(parentEntry)

                        AddGoalScreen(
                            viewModel = vm,
                            onBack = { navController.popBackStack() },
                            onSaveComplete = { navController.popBackStack() }
                        )
                    }
                }

                composable(Screen.Settings.route) {
                    val loginViewModel = hiltViewModel<SettingsViewModel>()
                    SettingsScreen(loginViewModel) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    }
                }
            }
        }
    }
}