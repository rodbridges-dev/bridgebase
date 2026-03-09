package com.bridgebase.bridgebase.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bridgebase.bridgebase.BuildConfig
import com.bridgebase.bridgebase.R
import com.bridgebase.bridgebase.ui.components.BridgeBaseCard

/**
 * ---------------------------------------------------------
 * SETTINGS SCREEN
 * ---------------------------------------------------------
 *
 * A clean, modular settings page used across templates.
 *
 * Features:
 *  - About section (shows app version/build)
 *  - Account section (logout logic + confirmation dialog)
 *  - Preferences section (UI-only toggles for dark mode / notif)
 *  - Support section (contact + terms)
 *
 * Logout uses ViewModel-driven state to keep business logic
 * out of the Composable layer and maintain unidirectional flow.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navigateToLogin: () -> Unit
) {
    // Controls whether the logout confirmation dialog is visible.
    var showLogoutDialog by remember { mutableStateOf(false) }

    // UI subscribes to logout events from the ViewModel.
    val logoutEvent by viewModel.logoutEvent.collectAsState()

    /**
     * When logoutEvent becomes true, navigate back to the Login screen.
     * This ensures ViewModel → UI unidirectional state flow.
     */
    LaunchedEffect(logoutEvent) {
        if (logoutEvent) navigateToLogin()
    }

    // Logout confirmation dialog.
    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    /**
     * Master container for all settings sections.
     * Uses LazyColumn for smooth scrolling and spacing consistency.
     */
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
    ) {
        item { Spacer(Modifier.height(24.dp)) }

        item {
            Text(
                text = stringResource(R.string.settings),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item { Spacer(Modifier.height(24.dp)) }

        // About (version info)
        item { AboutSection() }

        item { Spacer(Modifier.height(24.dp)) }

        // Account (logout)
        item { AccountSection(onLogoutClick = { showLogoutDialog = true }) }

        item { Spacer(Modifier.height(24.dp)) }

        // Preferences (UI-only toggles)
        item { PreferencesSection() }

        item { Spacer(Modifier.height(24.dp)) }

        // Support (email / terms)
        item { SupportSection() }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

/**
 * ---------------------------------------------------------
 * ABOUT SECTION
 * ---------------------------------------------------------
 *
 * Shows build version + code using values from BuildConfig.
 * Good for QA, builds, and white-label clients.
 */
@Composable
private fun AboutSection() {
    BridgeBaseCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                stringResource(R.string.about),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
            LabelValueRow(
                stringResource(R.string.version),
                "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                secondaryValue = true
            )
        }
    }
}

/**
 * ---------------------------------------------------------
 * ACCOUNT SECTION (LOGOUT)
 * ---------------------------------------------------------
 *
 * Handles logout UI.
 * Logout logic happens inside ViewModel to avoid mixing
 * business logic and Compose UI.
 */
@Composable
private fun AccountSection(
    onLogoutClick: () -> Unit
) {
    BridgeBaseCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                stringResource(R.string.account),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.accountdisclaimer),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.logout), fontSize = 18.sp)
            }
        }
    }
}

/**
 * ---------------------------------------------------------
 * PREFERENCES SECTION
 * ---------------------------------------------------------
 *
 * UI-only toggles; not wired to DataStore.
 * Buyers can extend them easily.
 */
@Composable
private fun PreferencesSection() {
    BridgeBaseCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                stringResource(R.string.preferences),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))

            PreferenceToggleRow(stringResource(R.string.darkmode), initial = false, onToggle = {})
            Spacer(Modifier.height(12.dp))
            PreferenceToggleRow(stringResource(R.string.notifications), initial = true, onToggle = {})
        }
    }
}

/**
 * ---------------------------------------------------------
 * SUPPORT SECTION
 * ---------------------------------------------------------
 *
 * Placeholder values for template/demo use.
 * Buyers can hook these into email intents or web screens.
 */
@Composable
private fun SupportSection() {
    BridgeBaseCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                stringResource(R.string.support),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
            LabelValueRow(stringResource(R.string.contactsupport), "support@example.com", secondaryValue = true)
            Spacer(Modifier.height(8.dp))
            LabelValueRow(stringResource(R.string.terms), stringResource(R.string.view), secondaryValue = true)
        }
    }
}

/**
 * ---------------------------------------------------------
 * LOGOUT CONFIRMATION DIALOG
 * ---------------------------------------------------------
 *
 * Keeps UI logic simple and reusable.
 * ViewModel handles the actual logout process.
 */
@Composable
private fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.logout) + "?") },
        text = { Text(stringResource(R.string.logoutdisclaimer)) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.logout)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

// -----------------------------------------------------------
// REUSABLE COMPONENTS
// -----------------------------------------------------------

/**
 * A reusable row for label/value pairs (e.g., version, email).
 */
@Composable
private fun LabelValueRow(
    label: String,
    value: String,
    secondaryValue: Boolean = false
) {
    val valueColor =
        if (secondaryValue) MaterialTheme.colorScheme.onSurfaceVariant
        else MaterialTheme.colorScheme.onSurface

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyLarge, color = valueColor)
    }
}

/**
 * Simple toggle switch row used for UI-only preference items.
 */
@Composable
private fun PreferenceToggleRow(
    label: String,
    initial: Boolean,
    onToggle: (Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(initial) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
                onToggle(it)
            }
        )
    }
}

