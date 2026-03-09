package com.bridgebase.bridgebase.navigation

sealed class Screen(val route: String) {

    // --- Root Level ---
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home")
    object Settings: Screen("settings")

    // --- Journal Graph ---
    object Journal : Screen("journal")
    object AddJournal : Screen("add_journal")

    data class JournalDetail(val entryId: String)
        : Screen("journal_detail/$entryId") {
        companion object {
            const val ARG = "entryId"
            const val ROUTE = "journal_detail/{$ARG}"
        }
    }

    // --- Goals Graph ---
    object Goals : Screen("goals")
    object AddGoal : Screen("add_goal")

    data class GoalDetail(val goalId: String)
        : Screen("goal_detail/$goalId") {
        companion object {
            const val ARG = "goalId"
            const val ROUTE = "goal_detail/{$ARG}"
        }
    }
}