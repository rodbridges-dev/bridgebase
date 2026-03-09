package com.bridgebase.bridgebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bridgebase.bridgebase.navigation.NavGraph
import com.bridgebase.bridgebase.ui.theme.BridgeBaseTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity entry point for the BridgeBase app.
 *
 * Integrates with Hilt dependency injection and sets up the Jetpack Compose
 * content tree. Applies the app's [BridgeBaseTheme] and attaches the
 * navigation graph defined in [NavGraph].
 *
 * Lifecycle:
 *  - Displays Android 12+ splash screen before Compose loads.
 *  - Enables edge-to-edge system drawing for modern layout behavior.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install Android 12 splash screen for smooth startup
        installSplashScreen()

        // Allow content to draw behind system bars
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        // Set Compose UI hierarchy with app theme and navigation graph
        setContent {
            BridgeBaseTheme {
                NavGraph()
            }
        }
    }
}