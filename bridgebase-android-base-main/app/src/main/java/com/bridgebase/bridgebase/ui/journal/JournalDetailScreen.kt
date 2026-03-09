package com.bridgebase.bridgebase.ui.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bridgebase.bridgebase.common.enums.Mood
import com.bridgebase.bridgebase.domain.JournalEntry
import com.bridgebase.bridgebase.ui.components.BridgeBaseCard
import com.bridgebase.bridgebase.ui.components.toFormattedDateString

/**
 * JournalDetailScreen
 *
 * Displays a single journal entry in a clean, readable format.
 * Includes:
 * - Title (in app bar)
 * - Mood & date header
 * - Full entry text with increased line height for readability
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalDetailScreen(
    entry: JournalEntry,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // Use ellipsis to avoid overflow on long titles
                    Text(
                        entry.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(10.dp))
                BridgeBaseCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        // ────────────────────────────────────────────────
                        // MOOD + DATE ROW
                        // ────────────────────────────────────────────────
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = Mood.fromRawValue(entry.mood)?.emoji ?: "",
                                fontSize = 30.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = entry.date.toFormattedDateString(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ────────────────────────────────────────────────
                        // FULL ENTRY TEXT
                        // Increased line height + natural paragraph breaks
                        // ────────────────────────────────────────────────
                        Text(
                            text = entry.entry.replace("\n", "\n\n"),
                            fontSize = 17.sp,
                            lineHeight = 26.sp,
                            letterSpacing = 0.15.sp
                        )
                    }
                }
            }
        }
    }
}