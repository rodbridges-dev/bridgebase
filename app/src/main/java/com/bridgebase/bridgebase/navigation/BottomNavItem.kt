package com.bridgebase.bridgebase.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Defines the bottom navigation bar items used throughout the BridgeBase app.
 *
 * Each object represents a tab in the navigation bar with an associated route,
 * title, and Material icon. The list of all items is accessible via [allItems].
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    /** Home screen tab */
    object Home : BottomNavItem(Screen.Home.route, "Home", Icons.Default.Home)

    /** Journal screen tab */
    object Journal : BottomNavItem(Screen.Journal.route, "Journal", Icons.Default.Edit)

    /** Goals screen tab */
    object Goals : BottomNavItem(Screen.Goals.route, "Goals", Icons.Default.Flag)

    /** User profile tab */
    object Profile : BottomNavItem(Screen.Settings.route, "Profile", Icons.Default.Person)

    companion object {
        /** List of all active tabs displayed in the bottom navigation bar */
        val allItems = listOf(Home, Journal, Goals, Profile)
    }
}
