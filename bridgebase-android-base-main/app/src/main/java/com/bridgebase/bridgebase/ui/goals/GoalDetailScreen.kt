package com.bridgebase.bridgebase.ui.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bridgebase.bridgebase.domain.GoalItem
import com.bridgebase.bridgebase.ui.components.toFormattedDateString
import com.bridgebase.bridgebase.R

/**
 * GoalDetailScreen
 *
 * Displays a full-page view of a single goal, including:
 *  - Title
 *  - Progress percentage + indicator
 *  - Target date
 *  - Optional notes
 *
 * This screen reads from an immutable GoalItem passed in via navigation.
 * It intentionally has no ViewModel because:
 *  - The data is read-only
 *  - All editing happens on the Add/Edit screens
 *  - Keeps detail screens lightweight and reusable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goal: GoalItem,
    onBack: () -> Unit
) {
    // Basic page structure with Material3 scaffold + top app bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.goaldetails)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(Modifier.height(24.dp))

            // -------------------------------------------------------
            // GOAL TITLE
            // -------------------------------------------------------
            Text(
                text = goal.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // -------------------------------------------------------
            // PROGRESS LABEL (e.g., “60% complete”)
            // -------------------------------------------------------
            Text(
                text = stringResource(
                    R.string.percentcomplete,
                    (goal.progress * 100).toInt()
                ),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            // -------------------------------------------------------
            // PROGRESS INDICATOR
            // Rounded progress bar to match the app’s card styling
            // -------------------------------------------------------
            LinearProgressIndicator(
                progress = { goal.progress },
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            )

            Spacer(Modifier.height(28.dp))

            // -------------------------------------------------------
            // TARGET DATE SECTION
            // -------------------------------------------------------
            Text(
                text = stringResource(R.string.targetdate),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Only render if a target date exists
            goal.targetDate?.let { targetDate ->
                Text(
                    text = targetDate.toFormattedDateString(),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.height(28.dp))

            // -------------------------------------------------------
            // NOTES SECTION
            // -------------------------------------------------------
            Text(
                text = stringResource(R.string.notes),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = goal.notes.ifBlank { stringResource(R.string.nonotesadded) },
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}
