package com.bridgebase.bridgebase.ui.signup

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.bridgebase.bridgebase.ui.login.loginTextFieldColors
import com.bridgebase.bridgebase.utils.AppBrand
import kotlinx.coroutines.launch

/**
 * SignupScreen
 *
 * Jetpack Compose screen for user registration as part of the BridgeBase template.
 *
 * Features:
 * - Gradient background that adapts to light/dark theme.
 * - Three Material3 OutlinedTextFields (Full name, Email, Password).
 * - Password visibility toggle with trailing icon.
 * - Form validation and loading state driven by [SignupViewModel].
 * - Error handling via a reusable [WarningBanner] shown with enter/exit animations.
 * - Navigation callbacks for:
 *   - Proceeding to the home screen after successful signup.
 *   - Returning to the login screen if the user already has an account.
 *
 * This screen mirrors the visual style of the Login screen to provide a consistent
 * authentication experience in the template.
 *
 * @param viewModel ViewModel providing form state, validation, and signup logic.
 * @param navigateToHome Callback invoked when signup succeeds and the app should
 *                       navigate to the main/home destination.
 * @param navigateBackToLogin Callback invoked when the user taps the “Log In” text
 *                            on the bottom prompt to return to the login screen.
 * @param isDarkTheme Optional override for theme detection; defaults to system theme.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SignupScreen(
    viewModel: SignupViewModel,
    navigateToHome: () -> Unit,
    navigateBackToLogin: () -> Unit,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    // Coroutine scope used for calling suspend functions in response to UI events.
    val scope = rememberCoroutineScope()

    // ----- ViewModel-backed form state -----
    val nameState by viewModel.name.observeAsState("")
    val emailState by viewModel.email.observeAsState("")
    val passwordState by viewModel.pass.observeAsState("")
    val isFormValid by viewModel.isValid.observeAsState(false)

    // ----- ViewModel-backed UI feedback state -----
    val isLoading by viewModel.isLoading.collectAsState()
    val navigateHome by viewModel.navigateToHome.collectAsStateWithLifecycle()
    val showDialogState by viewModel.showDialog.collectAsStateWithLifecycle()
    val dialogText by viewModel.dialogText.observeAsState("")

    // Password visibility is UI-only state, preserved across recompositions.
    var passwordVisiblity by rememberSaveable { mutableStateOf(false) }

    // Used to move focus between fields and dismiss keyboard on submit.
    val focusManager = LocalFocusManager.current

    // Primary click handler for the "Sign Up" button and IME Done action.
    val signupClick = {
        scope.launch {
            viewModel.signup(nameState, emailState, passwordState)
        }
    }

    // Once the ViewModel reports navigation = true, trigger the navigation callback,
    // then reset the flag to avoid repeated navigation.
    LaunchedEffect(navigateHome) {
        if (navigateHome) {
            navigateToHome()
            viewModel.setNavigateToHome(false)
        }
    }

    // Background gradient aligned with the login screen, with light/dark variants.
    val gradient = Brush.linearGradient(
        listOf(
            if (isDarkTheme) AppBrand.gradientStartLoginDark else AppBrand.gradientStartLoginLight,
            if (isDarkTheme) AppBrand.gradientEndLoginDark else AppBrand.gradientEndLoginLight
        )
    )

    // Root container that holds the entire signup layout.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(horizontal = 32.dp)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

        // Main column that vertically stacks logo, header, form fields, and footer text.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .imeNestedScroll()   // optional but very smooth
                .verticalScroll(rememberScrollState())
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /** Logo header **/
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier.height(64.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Screen title text, e.g. "Create Your Account".
            Text(
                text = stringResource(id = R.string.createnewaccount),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 40.dp)
            )

            // Spacer to push the form towards the vertical center of the screen.
            Spacer(modifier = Modifier.weight(1f))

            // ----- Input fields block -----
            Column(modifier = Modifier.imePadding()) {

                // FULL NAME
                OutlinedTextField(
                    value = nameState,
                    onValueChange = { viewModel.setName(it) },
                    label = { Text(stringResource(id = R.string.fullname)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = loginTextFieldColors(isDarkTheme)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // EMAIL
                OutlinedTextField(
                    value = emailState,
                    onValueChange = { viewModel.setEmail(it) },
                    label = { Text(stringResource(id = R.string.email)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = loginTextFieldColors(isDarkTheme)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PASSWORD (with visibility toggle)
                OutlinedTextField(
                    value = passwordState,
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text(stringResource(id = R.string.password)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    visualTransformation = if (passwordVisiblity) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        // Eye / eye-off icon that toggles password visibility.
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
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        // Dismiss keyboard and attempt signup when user hits Done.
                        focusManager.clearFocus()
                        signupClick()
                    }),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = loginTextFieldColors(isDarkTheme)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            /** SIGNUP BUTTON **/

            // Button color adapts to dark mode and enabled/disabled state.
            val buttonColor = when {
                isFormValid -> MaterialTheme.colorScheme.primary
                isDarkTheme -> AppBrand.buttonDisabledDark
                else -> AppBrand.buttonDisabledLight
            }

            Button(
                onClick = {
                    focusManager.clearFocus()   // 🔹 Dismiss keyboard
                    signupClick()
                },
                enabled = isFormValid == true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = if (isFormValid) Color.White else Color.Gray,
                    disabledContainerColor = buttonColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp), ambientColor = Color.Black.copy(0.15f))
            ) {
                if (isLoading) {
                    // Inline progress indicator when the signup request is in-flight.
                    CircularProgressIndicator(
                        color = Color(0xFF3B63FF).copy(alpha = 0.25f),
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(id = R.string.signup), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Inline navigation prompt back to the login screen.
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.alreadymember),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )

                TextButton(onClick = { navigateBackToLogin() }) {
                    Text(
                        text = stringResource(id = R.string.loginhere),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms/consent text aligned with the login screen footer style.
            Text(
                text = stringResource(id = R.string.continueterms),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Spacer to keep footer anchored near the bottom on taller devices.
            Spacer(modifier = Modifier.weight(1f))
        }

        // Animated warning banner that appears at the top of the screen
        // when the ViewModel exposes an error message.
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
                message = dialogText,
                onDismiss = { viewModel.onDialogDismiss() },
            )
        }
    }
}
