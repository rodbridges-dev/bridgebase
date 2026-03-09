package com.bridgebase.bridgebase.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bridgebase.bridgebase.navigation.BottomNavItem
import com.bridgebase.bridgebase.utils.AppBrand

/**
 * Displays the bottom navigation bar used across the BridgeBase app.
 *
 * Automatically highlights the current active route and allows users
 * to navigate between primary app sections (Home, Journal, Goals, Chat, Profile).
 *
 * @param navController Navigation controller for route management.
 */
@Composable
fun BottomNavBar(
    navController: NavHostController
) {
    // List of all available bottom navigation tabs
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Journal,
        BottomNavItem.Goals,
        BottomNavItem.Profile
    )

    // Track the current visible destination
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = AppBrand.primary
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                icon = {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppBrand.onPrimaryLight,
                    unselectedIconColor = AppBrand.onPrimaryLight.copy(alpha = 0.6f),
                    selectedTextColor = AppBrand.onPrimaryLight,
                    unselectedTextColor = AppBrand.onPrimaryLight.copy(alpha = 0.6f),
                    indicatorColor = AppBrand.onPrimaryLight.copy(alpha = 0.2f)
                )
            )
        }
    }
}