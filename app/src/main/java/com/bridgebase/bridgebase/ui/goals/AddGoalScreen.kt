package com.bridgebase.bridgebase.ui.goals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bridgebase.bridgebase.R
import com.bridgebase.bridgebase.ui.components.BridgeBaseCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * AddGoalScreen
 *
 * This screen allows users to create a new goal by entering:
 *  - Title
 *  - Target date
 *  - Optional notes
 *
 * It follows a clean UI-driven architecture:
 *  - ViewModel stores screen state + business logic
 *  - Composable collects state via collectAsStateWithLifecycle()
 *  - UI events call ViewModel update functions
 *  - On completion, parent navigation handles transitions
 *
 * This keeps UI code declarative, predictable, and easy to extend.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    viewModel: GoalsViewModel,
    onBack: () -> Unit,
    onSaveComplete: () -> Unit
) {

    // -------------------------------------------------------
    // ViewModel STATE (updated automatically via Flows)
    // -------------------------------------------------------
    val title by viewModel.title.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val targetDate by viewModel.targetDate.collectAsStateWithLifecycle()

    // Controls visibility of the Material3 Date Picker Dialog
    var showDatePicker by remember { mutableStateOf(false) }

    // Picker defaults to "today" to improve UX
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    // Reset the ViewModel when the composable leaves the composition
    DisposableEffect(Unit) {
        onDispose {
            viewModel.reset()
        }
    }

    // -------------------------------------------------------
    // DATE PICKER DIALOG
    // -------------------------------------------------------
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDateSelected(
                            datePickerState.selectedDateMillis
                        )
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // -------------------------------------------------------
    // MAIN LAYOUT
    // -------------------------------------------------------
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.addnewgoal), fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.reset()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(Modifier.height(16.dp))

            // -------------------------------------------------------
            // TITLE INPUT CARD
            // -------------------------------------------------------
            BridgeBaseCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.goaltitle),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.updateTitle(it) },
                        placeholder = { Text(stringResource(R.string.addnewgoaldefault)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // -------------------------------------------------------
            // TARGET DATE CARD
            // -------------------------------------------------------
            BridgeBaseCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.targetdate),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = targetDate?.let {
                                // FIX: Set TimeZone to UTC before formatting
                                val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.US).apply {
                                    timeZone = TimeZone.getTimeZone("UTC") // <-- ADD THIS LINE
                                }
                                formatter.format(Date(it))
                            } ?: stringResource(R.string.selectadate)
                        )
                    }

                }
            }

            Spacer(Modifier.height(16.dp))

            // -------------------------------------------------------
            // NOTES CARD
            // -------------------------------------------------------
            BridgeBaseCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.notes),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { viewModel.updateNotes(it) },
                        placeholder = { Text(stringResource(R.string.optionalnnotes)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // -------------------------------------------------------
            // SAVE BUTTON
            // -------------------------------------------------------
            Button(
                onClick = {
                    viewModel.saveGoal()
                    viewModel.reset()
                    onSaveComplete()
                },
                enabled = title.isNotBlank() && notes.isNotBlank() && targetDate != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 22.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.savegoal), fontSize = 18.sp)
            }

            Spacer(Modifier.height(60.dp))
        }

    }
}
