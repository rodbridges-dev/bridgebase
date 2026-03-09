package com.bridgebase.bridgebase.ui.goals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bridgebase.bridgebase.R
import com.bridgebase.bridgebase.domain.GoalItem
import com.bridgebase.bridgebase.ui.journal.PremiumGradientFAB
import com.bridgebase.bridgebase.ui.components.BridgeBaseCard
import com.bridgebase.bridgebase.ui.components.GoalShimmer
import com.bridgebase.bridgebase.utils.AppBrand

/**
 * GoalsScreen
 *
 * Main goal management screen. Displays three categorized lists:
 *  - In Progress goals
 *  - Upcoming goals
 *  - Completed goals
 *
 * This screen demonstrates:
 *  - Reactive UI with Flow -> State (collectAsStateWithLifecycle)
 *  - Clean separation of UI and ViewModel logic
 *  - Styled empty states
 *  - Shimmer placeholders during loading
 *  - Navigation callbacks to Add & Detail screens
 *
 * This is one of the centerpiece screens clients will judge your
 * Jetpack Compose expertise on, so clarity and structure matter.
 */
@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel,
    navigateToAddGoal: () -> Unit,
    navigateToDetail: (String) -> Unit
) {

    // -------------------------------------------------------
    // STATE FROM VIEWMODEL (reactive lists)
    // -------------------------------------------------------
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val inProgress by viewModel.inProgress.collectAsStateWithLifecycle()
    val upcoming by viewModel.upcoming.collectAsStateWithLifecycle()
    val completed by viewModel.completed.collectAsStateWithLifecycle()

    // Combined empty state for all categories
    val isEmpty = inProgress.isEmpty() && upcoming.isEmpty() && completed.isEmpty()

    // Remove default insets so layout matches iOS exactly
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            PremiumGradientFAB(
                onClick = navigateToAddGoal,
                modifier = Modifier
                    .padding(bottom = 22.dp, end = 16.dp) // PERFECT POSITIONING
            )
        }
    ) { padding ->

        // -------------------------------------------------------
        // MAIN LIST
        // -------------------------------------------------------
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(
                    start = padding.calculateStartPadding(LayoutDirection.Ltr),
                    end = padding.calculateEndPadding(LayoutDirection.Ltr)
                )
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // -------------------------------------------------------
            // MAIN HEADER
            // -------------------------------------------------------
            item {
                Text(
                    text = stringResource(R.string.yourgoalsheader),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // -------------------------------------------------------
            // EMPTY STATE (SHOWN ONLY AFTER LOADING)
            // -------------------------------------------------------
            if (!isLoading && isEmpty) {

                item {
                    EmptyGoalsView(onAddGoalClicked = navigateToAddGoal)
                }

            } else {

                // -------------------------------------------------------
                // IN PROGRESS SECTION
                // -------------------------------------------------------
                item {
                    SectionHeader(stringResource(R.string.inprogress))
                    Spacer(Modifier.height(12.dp))
                }

                if (isLoading) {
                    items(2) {
                        GoalShimmer()
                        Spacer(Modifier.height(12.dp))
                    }
                } else if (inProgress.isEmpty()) {
                    item { EmptyGoalRow(stringResource(R.string.noactivegoals)) }
                } else {
                    items(inProgress) { goal ->
                        GoalCard(goal) { navigateToDetail(goal.id) }
                        Spacer(Modifier.height(12.dp))
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }

                // -------------------------------------------------------
                // UPCOMING SECTION
                // -------------------------------------------------------
                item {
                    SectionHeader(stringResource(R.string.upcoming))
                    Spacer(Modifier.height(12.dp))
                }

                if (isLoading) {
                    items(2) {
                        GoalShimmer()
                        Spacer(Modifier.height(12.dp))
                    }
                } else if (upcoming.isEmpty()) {
                    item { EmptyGoalRow(stringResource(R.string.youhaveupcoming)) }
                } else {
                    items(upcoming) { goal ->
                        GoalCard(goal) { navigateToDetail(goal.id) }
                        Spacer(Modifier.height(12.dp))
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }

                // -------------------------------------------------------
                // COMPLETED SECTION
                // -------------------------------------------------------
                item {
                    SectionHeader(stringResource(R.string.completed))
                    Spacer(Modifier.height(12.dp))
                }

                if (isLoading) {
                    items(2) {
                        GoalShimmer()
                        Spacer(Modifier.height(12.dp))
                    }
                } else if (completed.isEmpty()) {
                    item { EmptyGoalRow(stringResource(R.string.nocompletegoals)) }
                } else {
                    items(completed) { goal ->
                        GoalCard(goal) { navigateToDetail(goal.id) }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            // Space so FAB doesn't overlap content
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

//
// ---------------------------------------------------------------
// HELPER UI COMPONENTS
// ---------------------------------------------------------------
//
@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
    )
}

/**
 * Card representation of a single goal in the list.
 * Tappable to navigate into GoalDetailScreen.
 */
@Composable
private fun GoalCard(
    goal: GoalItem,
    onClick: () -> Unit
) {
    BridgeBaseCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {

            // Goal title
            Text(
                text = goal.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { goal.progress },
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress label (e.g., “40% complete”)
            Text(
                text = stringResource(
                    R.string.percentcomplete,
                    (goal.progress * 100).toInt()
                ),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Small inline empty-state row shown under each category.
 */
@Composable
private fun EmptyGoalRow(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
}

/**
 * Full-page empty state shown when the user has no goals at all.
 * Includes a branded gradient CTA button.
 */
@Composable
fun EmptyGoalsView(
    onAddGoalClicked: () -> Unit,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme()
) {

    // Dynamic gradients depending on light/dark mode
    val gradientColors = listOf(
        if (isSystemInDarkTheme) AppBrand.gradientStartDark else AppBrand.gradientStartLight,
        if (isSystemInDarkTheme) AppBrand.gradientEndDark else AppBrand.gradientEndLight
    )

    Spacer(Modifier.height(24.dp))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .background(
                if (isSystemInDarkTheme) AppBrand.emptyStateBackgroundDark
                else AppBrand.emptyStateBackgroundLight,
                RoundedCornerShape(16.dp)
            )
            .border(
                BorderStroke(
                    1.dp,
                    if (isSystemInDarkTheme) AppBrand.emptyStateBorderDark
                    else AppBrand.emptyStateBorderLight
                ),
                RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {

        // Emoji icon for friendly empty state
        Text(
            text = "🎯",
            fontSize = 38.sp
        )

        Text(
            text = stringResource(R.string.nogoalsyet),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.setfirstgoal),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Primary CTA with gradient background
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            onClick = onAddGoalClicked,
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(colors = gradientColors),
                    shape = RoundedCornerShape(25.dp)
                ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = stringResource(R.string.addagoal),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}
