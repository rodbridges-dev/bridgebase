package com.bridgebase.bridgebase.ui.forgot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bridgebase.bridgebase.R
import com.bridgebase.bridgebase.ui.components.WarningBanner

/**
 * Forgot Password screen composable.
 *
 * Allows users to request a password-reset link by entering their email.
 * Integrates with [ForgotPasswordViewModel] for validation and feedback.
 *
 * @param viewModel The associated ViewModel handling email input and reset logic.
 * @param navigateBack Called when the user taps “Back to Login”.
 * @param isDarkTheme Optional override to test dark mode appearance.
 */
@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    navigateBack: () -> Unit,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    // Observe ViewModel states
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val showDialogState: Boolean by viewModel.showDialog.collectAsStateWithLifecycle()
    val dialogText by viewModel.dialogText.observeAsState()

    val emailState by viewModel.email.observeAsState()
    val isFormValid by viewModel.isValid.observeAsState()

    val focusManager = LocalFocusManager.current

    // Lambda to trigger password-reset request
    val forgotClick = {
        viewModel.sendResetLink(emailState ?: "")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 32.dp, vertical = 40.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            /** Header section */
            Text(
                text = stringResource(id = R.string.resetpassword),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 80.dp, bottom = 24.dp)
            )

            Text(
                text = stringResource(id = R.string.sendresetlink),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            /** Email input field */
            OutlinedTextField(
                value = emailState ?: "",
                onValueChange = { viewModel.setEmail(it) },
                label = { Text(stringResource(id = R.string.email), color = Color.White) },
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    forgotClick()
                }),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(Modifier.height(32.dp))

            /** Submit button */
            Button(
                onClick = { forgotClick() },
                enabled = isFormValid == true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(stringResource(id = R.string.sendlink), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            /** Navigation back to login */
            TextButton(onClick = { navigateBack() }) {
                Text(stringResource(id = R.string.backtologin), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
            }
        }

        /** Warning banner for success or error messages */
        AnimatedVisibility(
            visible = showDialogState,
            enter = slideInVertically(initialOffsetY = { -40 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -40 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
        ) {
            WarningBanner(
                title = stringResource(id = R.string.headsup),
                message = dialogText ?: "",
                onDismiss = { viewModel.onDialogDismiss() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp)
            )
        }
    }
}