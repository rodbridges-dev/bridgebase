package com.bridgebase.bridgebase.ui.journal

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bridgebase.bridgebase.common.enums.Mood
import com.bridgebase.bridgebase.utils.AppBrand
import com.bridgebase.bridgebase.R

/**
 * AddJournalScreen
 *
 * This screen allows the user to create a new journal entry.
 * It includes:
 * - Title input
 * - Content input
 * - Mood selection
 * - Save button with gradient (disabled until form valid)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJournalScreen(
    viewModel: JournalViewModel,
    onBack: () -> Unit,
    onSaveComplete: () -> Unit,
    isDark: Boolean = isSystemInDarkTheme()
) {
    // Local UI state
    val title = remember { mutableStateOf("") }
    val content = remember { mutableStateOf("") }
    val selectedMood = remember { mutableStateOf<Mood?>(null) }

    // Form validation
    val isFormValid = title.value.isNotBlank() &&
            content.value.isNotBlank() &&
            selectedMood.value != null

    // Brand gradient colors
    val gradientColors = listOf(
        if (isDark) AppBrand.gradientStartDark else AppBrand.gradientStartLight,
        if (isDark) AppBrand.gradientEndDark else AppBrand.gradientEndLight
    )

    // Button gradient states
    val enabledBrush = Brush.horizontalGradient(gradientColors)
    val disabledBrush = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surfaceVariant
        )
    )

    val (titleFocus, contentFocus) = remember { FocusRequester.createRefs() }
    val focusManager = LocalFocusManager.current

    val lightInputBackground = Color(0xFFF5F6FA)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.newentry)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ────────────────────────────────────────────────
            // Title Field
            // ────────────────────────────────────────────────
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text(stringResource(R.string.title)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(titleFocus),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions( // 👈 7. CONFIGURE KEYBOARD
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next // Set the action button to "Next"
                ),
                keyboardActions = KeyboardActions( // 👈 8. DEFINE THE ACTION
                    onNext = { contentFocus.requestFocus() } // Move focus to the content field
                )
            )

            // ────────────────────────────────────────────────
            // Content Field
            // ────────────────────────────────────────────────
            OutlinedTextField(
                value = content.value,
                onValueChange = { content.value = it },
                label = { Text(stringResource(R.string.writeyourthoughts)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .focusRequester(contentFocus),
                maxLines = 10,
                shape = RoundedCornerShape(12.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = lightInputBackground,
                    unfocusedContainerColor = lightInputBackground,
                    disabledContainerColor = lightInputBackground
                ),
                keyboardOptions = KeyboardOptions( // 👈 10. CONFIGURE KEYBOARD
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done // Set the action button to "Done"
                ),
                keyboardActions = KeyboardActions( // 👈 11. DEFINE THE ACTION
                    onDone = { focusManager.clearFocus() } // Hide the keyboard
                )
            )


            // ────────────────────────────────────────────────
            // Mood Selection
            // ────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.mood),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp) // Optional: Adds nice padding at start/end
            ) {
                items(Mood.entries) { mood ->
                    MoodOption(
                        mood = mood,
                        isSelected = selectedMood.value == mood,
                        onSelected = { selectedMood.value = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ────────────────────────────────────────────────
            // SAVE BUTTON
            // Gradient background is handled with a Box wrapper
            // ────────────────────────────────────────────────
            Button(
                onClick = {
                    viewModel.addEntry(
                        title = title.value,
                        content = content.value,
                        mood = selectedMood.value
                    )
                    onSaveComplete()
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(
                        brush = if (isFormValid) enabledBrush else disabledBrush,
                        shape = RoundedCornerShape(30.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    text = stringResource(R.string.saveentry),
                    color = if (isFormValid) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * MoodOption
 *
 * This composable renders a single mood pill with:
 * - Scale animation when selected
 * - Gradient border if selected
 * - Filled pill background
 */
@Composable
fun MoodOption(
    mood: Mood,
    isSelected: Boolean,
    onSelected: (Mood) -> Unit,
    isDark: Boolean = isSystemInDarkTheme()
) {
    // Selection scale animation
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = tween(180)
    )

    // Brand gradient
    val gradientColors = listOf(
        if (isDark) AppBrand.gradientStartDark else AppBrand.gradientStartLight,
        if (isDark) AppBrand.gradientEndDark else AppBrand.gradientEndLight
    )

    Box(
        modifier = Modifier
            .padding(6.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(50)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush =
                    if (isSelected)
                        Brush.horizontalGradient(gradientColors)
                    else
                        SolidColor(MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(50)
            )
            .clickable { onSelected(mood) }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = mood.emoji,
                fontSize = if (isSelected) 26.sp else 22.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = mood.name.lowercase().replaceFirstChar { it.uppercase() },
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color =
                    if (isSelected)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
