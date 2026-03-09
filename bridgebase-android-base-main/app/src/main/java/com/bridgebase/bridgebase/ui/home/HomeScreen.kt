package com.bridgebase.bridgebase.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bridgebase.bridgebase.R
import com.bridgebase.bridgebase.domain.User
import com.bridgebase.bridgebase.ui.components.BridgeBaseCard
import com.bridgebase.bridgebase.ui.components.HomeShimmer
import com.bridgebase.bridgebase.ui.components.timeBasedGreeting
import com.bridgebase.bridgebase.utils.LottieAnimationWithDelay

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToJournal: () -> Unit,
    navigateToGoals: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val userInfo by viewModel.currentUser.collectAsStateWithLifecycle()
    val journalSummary by viewModel.journalSummary.collectAsStateWithLifecycle()

    if (isLoading) {
        HomeShimmer()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp) // global top padding
        ) {

            // -----------------------------------------------------
            // GREETING HEADER
            // -----------------------------------------------------
            HeaderSection(userInfo)

            Spacer(modifier = Modifier.height(24.dp))

            // -----------------------------------------------------
            // DAILY JOURNAL CTA CARD
            // -----------------------------------------------------
            BridgeBaseCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navigateToJournal() }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        // Animated icon
                        LottieAnimationWithDelay(
                            animationRes = R.raw.largeflame,
                            modifier = Modifier.size(34.dp),
                            iterations = 3,
                            delayMillis = 500,
                            loop = true
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = stringResource(R.string.journaltoday),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    journalSummary?.let {
                        Text(
                            text = it.message,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // -----------------------------------------------------
            // GOALS SUMMARY CARD
            // -----------------------------------------------------
            GoalsSummaryCard(
                inProgress = 2,
                completed = 1,
                upcoming = 1,
                onClick = navigateToGoals
            )

            Spacer(modifier = Modifier.height(24.dp))

            // -----------------------------------------------------
            // RECENT ACTIVITY
            // -----------------------------------------------------
            Text(
                text = stringResource(R.string.recentactivity),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            journalSummary?.let { summary ->

                BridgeBaseCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        // Header row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(userInfo?.avatar ?: R.drawable.avatar),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = stringResource(R.string.makingprogress),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        if (summary.recentActivity.isEmpty()) {
                            Text(
                                text = stringResource(R.string.norecentactivity),
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        } else {
                            summary.recentActivity.forEach { item ->
                                ActivityRow(item.subtitle)
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// -----------------------------------------------------
// HEADER BLOCK
// -----------------------------------------------------
@Composable
private fun HeaderSection(userInfo: User?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        userInfo?.let { currentUser ->
            Image(
                painter = painterResource(currentUser.avatar),
                contentDescription = currentUser.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {

            userInfo?.let { currentUser ->
                Text(
                    text = timeBasedGreeting(currentUser.name),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currentUser.tipOfDay,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// -----------------------------------------------------
// ACTIVITY ROW
// -----------------------------------------------------
@Composable
fun ActivityRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF3A7AFE))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 15.sp
        )
    }
}

// -----------------------------------------------------
// GOALS SUMMARY CARD
// -----------------------------------------------------
@Composable
fun GoalsSummaryCard(
    inProgress: Int,
    completed: Int,
    upcoming: Int,
    onClick: () -> Unit
) {
    BridgeBaseCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFFDFF8E7),
                            Color(0xFFC6F0D9)
                        )
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    text = stringResource(R.string.yourgoals),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GoalsStat(stringResource(R.string.inprogress), inProgress, Icons.Default.Flag)
                    GoalsStat(stringResource(R.string.completed), completed, Icons.Default.Check)
                    GoalsStat(stringResource(R.string.upcoming), upcoming, Icons.Default.Schedule)
                }
            }
        }
    }
}

@Composable
fun GoalsStat(label: String, value: Int, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF2A7DE1).copy(alpha = 0.85f),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6A6A6A)
        )
    }
}
