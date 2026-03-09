package com.bridgebase.bridgebase.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bridgebase.bridgebase.R
import com.bridgebase.bridgebase.ui.components.WarningBanner
import com.bridgebase.bridgebase.ui.forgot.ForgotPasswordScreen
import com.bridgebase.bridgebase.utils.AppBrand
import com.bridgebase.bridgebase.ui.forgot.ForgotPasswordViewModel
import kotlinx.coroutines.launch

/**
 * LoginScreen
 *
 * Jetpack Compose authentication screen included in the BridgeBase Android template.
 * Provides a complete email/password login UI built with Material 3.
 *
 * Features:
 * - Email & password fields with validation and password visibility toggle.
 * - Firebase Auth integration via [LoginViewModel].
 * - Animated warning banner for error handling.
 * - Modal bottom sheet for password reset.
 * - Optional navigation to a signup screen.
 * - Gradient background with dark/light theme support.
 *
 * @param viewModel Handles login, validation, navigation state, and error messages.
 * @param forgotViewModel ViewModel used by the password-reset bottom sheet.
 * @param navigateToHome Callback triggered when login succeeds.
 * @param navigateToSignup Navigation callback for users who tap “Sign Up”.
 * @param isDarkTheme Optional override; defaults to system theme.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    forgotViewModel: ForgotPasswordViewModel,
    navigateToHome: () -> Unit,
    navigateToSignup: () -> Unit,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    // Coroutine scope for login calls.
    val scope = rememberCoroutineScope()

    // ----- ViewModel-backed form fields -----
    val emailState by viewModel.email.observeAsState()
    val passwordState by viewModel.pass.observeAsState()
    val isFormValid by viewModel.isValid.observeAsState()

    val focusManager = LocalFocusManager.current

    // Single handler for login action (button or keyboard IME)
    val loginClick = { email: String, password: String ->
        scope.launch {
            viewModel.login(email, password)
        }
    }

    // ----- Forgot password modal -----
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val modifyBottomSheet = { bool: Boolean ->
        openBottomSheet = bool
    }

    // ----- UI feedback / state -----
    val isLoading by viewModel.isLoading.collectAsState()
    val showDialogState: Boolean by viewModel.showDialog.collectAsStateWithLifecycle()
    val dialogText by viewModel.dialogText.observeAsState()
    var passwordVisiblity by rememberSaveable { mutableStateOf(false) }

    // Gradient background identical to SignupScreen for brand consistency.
    val gradient = Brush.linearGradient(
        colors = listOf(
            if (isDarkTheme) AppBrand.gradientStartLoginDark else AppBrand.gradientStartLoginLight,
            if (isDarkTheme) AppBrand.gradientEndLoginDark else AppBrand.gradientEndLoginLight,
        )
    )

    // Trigger navigation once Firebase Auth reports success.
    val navigateHome by viewModel.navigateToHome.collectAsStateWithLifecycle()
    LaunchedEffect(navigateHome) {
        if (navigateHome) {
            navigateToHome()
            viewModel.setNavigateToHome(false)
        }
    }

    // ----- Root Layout -----
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(horizontal = 32.dp)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /** Logo Header **/
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                /** Logo header **/
                Image(
                    painter = painterResource(id = R.drawable.logo), // your BridgeBase logo
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier.height(64.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Text(
                text = stringResource(id = R.string.welcometoapp),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 40.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // ----- Form Fields -----
            Column(modifier = Modifier.imePadding()) {

                // EMAIL INPUT
                OutlinedTextField(
                    value = emailState ?: "",
                    onValueChange = { viewModel.setEmail(it) },
                    label = { Text(stringResource(id = R.string.email)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = loginTextFieldColors(isDarkTheme)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PASSWORD INPUT
                OutlinedTextField(
                    value = passwordState ?: "",
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text(stringResource(id = R.string.password)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        loginClick(emailState ?: "", passwordState ?: "")
                    }),
                    visualTransformation = if (passwordVisiblity) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisiblity)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff

                        IconButton(onClick = {
                            passwordVisiblity = !passwordVisiblity
                        }) {
                            Icon(imageVector = image, "")
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = loginTextFieldColors(isDarkTheme)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            /** LOGIN BUTTON **/
            val buttonColor = when {
                isFormValid == true -> MaterialTheme.colorScheme.primary
                isDarkTheme -> AppBrand.buttonDisabledDark
                else -> AppBrand.buttonDisabledLight
            }

            Button(
                onClick = {
                    focusManager.clearFocus(force = true)   // 🔹 Dismiss keyboard immediately
                    loginClick(emailState ?: "", passwordState ?: "")
                },
                enabled = isFormValid == true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = if (isFormValid == true) Color.White else Color.Gray,
                    disabledContainerColor = buttonColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp), ambientColor = Color.Black.copy(0.15f))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFF3B63FF).copy(alpha = 0.25f),
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(id = R.string.login), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FORGOT PASSWORD
            TextButton(
                onClick = { modifyBottomSheet(true) }
            ) {
                Text(stringResource(id = R.string.forgotpassword), color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // SIGNUP LINK
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.notmember),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )

                TextButton(onClick = { navigateToSignup() }) {
                    Text(
                        text = stringResource(id = R.string.signup),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Terms of use footer
            Text(
                text = stringResource(id = R.string.continueterms),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

        }

        // ----- Animated Error Banner -----
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

    // ----- Forgot Password Modal -----
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { modifyBottomSheet(false) },
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.surface,
            dragHandle = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetDefaults.DragHandle()
                }
            }
        ) {
            ForgotPasswordScreen(
                viewModel = forgotViewModel,
                navigateBack = { modifyBottomSheet(false) }
            )
        }
    }

}

/** Returns themed color set for login text fields. */
@Composable
fun loginTextFieldColors(isDarkTheme: Boolean): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = if (isDarkTheme) colorResource(R.color.textFieldBgDark) else colorResource(R.color.textFieldBGLight),
        unfocusedContainerColor = if (isDarkTheme) colorResource(R.color.textFieldBgDark) else colorResource(R.color.textFieldBGLight),
        focusedBorderColor = MaterialTheme.colorScheme.outline,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    )
}
