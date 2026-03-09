package com.bridgebase.bridgebase.ui.journal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.South
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bridgebase.bridgebase.R
import com.bridgebase.bridgebase.domain.JournalEntry
import com.bridgebase.bridgebase.ui.components.BridgeBaseCard
import com.bridgebase.bridgebase.common.enums.Mood
import com.bridgebase.bridgebase.ui.components.JournalShimmerCard
import com.bridgebase.bridgebase.ui.components.toFormattedDateString
import com.bridgebase.bridgebase.utils.AppBrand


/**
 * JournalScreen
 *
 * Full-featured journal UI designed to showcase premium Compose patterns.
 *
 * Includes:
 *  - Daily reflection card
 *  - Search (with live filtering)
 *  - Mood filters
 *  - Grouped entries by month/year
 *  - Empty states + shimmer loading placeholders
 *  - Floating action button with gradient + press animation
 *
 * This module is built so buyers can easily replace storage (Firestore, Room,
 * REST) without touching the UI layer.
 */
@Composable
fun JournalScreen(
    viewModel: JournalViewModel,
    navigateToJournalDetail: (String) -> Unit,
    navigateToAddJournal: () -> Unit,
) {
    // Reactive UI state
    val journalEntries by viewModel.filteredEntries.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // Group entries by month-year (e.g., “Sept 2025”)
    val groupedEntries = remember(journalEntries) {
        viewModel.groupEntriesByMonthYear(journalEntries)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            PremiumGradientFAB(
                onClick = navigateToAddJournal,
                modifier = Modifier.padding(bottom = 22.dp, end = 16.dp)
            )
        }
    ) { padding ->

        // Main list container
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(
                    start = padding.calculateStartPadding(LayoutDirection.Ltr),
                    end = padding.calculateEndPadding(LayoutDirection.Ltr),
                )
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // --- Header ---
            item { JournalHeader() }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // --- Search Bar ---
            item { JournalSearchBar(viewModel) }
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // --- Daily Prompt ---
            item { ReflectionCard(viewModel) }
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // --- Filters ---
            item { JournalFilters(viewModel) }
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Section header
            item { SectionLabel(stringResource(R.string.recententries)) }

            // ------------------------------------------------------
            //  SEARCH MODE
            // ------------------------------------------------------
            if (isSearchActive) {
                    if (searchResults.isEmpty() && !isLoading) {
                        item { EmptyJournalView(navigateToAddJournal) }
                    } else {
                        items(
                            count = searchResults.size,
                            key = { index -> searchResults[index].id },
                            itemContent = { index ->
                                val record = searchResults[index]
                                JournalEntryCard(record) {
                                    navigateToJournalDetail(searchResults[index].id)
                                }
                                if (index < searchResults.size - 1) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        )
                    }
            } else {

                // ------------------------------------------------------
                //  NORMAL MODE (Grouped by Month)
                // ------------------------------------------------------

                if (isLoading) {
                    items(4) {
                        JournalShimmerCard()
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    item {
                        AnimatedVisibility(
                            visible = journalEntries.isEmpty(),
                            enter = fadeIn(tween(300)),
                            exit = fadeOut(tween(200))
                        ) {
                            EmptyJournalView(navigateToAddJournal)
                        }
                    }
                }
                groupedEntries.forEach { (monthYear, entries) ->

                    // Group header
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                    item {
                        Text(
                            text = monthYear,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Entries for month
                    items(
                        items = entries,
                        key = { it.id }
                    ) { entry ->

                        JournalEntryCard(entry) {
                            navigateToJournalDetail(entry.id)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Padding at bottom so FAB doesn’t cover content
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

/**
 * Premium FAB with gradient, glow, and press animation.
 * Buyers may reuse this component across the template.
 */
@Composable
fun PremiumGradientFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme()
) {

    // Press scale animation
    var pressed by remember { mutableStateOf(false) }

    // Scale-down animation when pressed
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = tween(150)
    )

    // Brand gradient colors
    val gradientColors = listOf(
        if (isDark) AppBrand.gradientStartDark else AppBrand.gradientStartLight,
        if (isDark) AppBrand.gradientEndDark else AppBrand.gradientEndLight
    )

    // Outer glow ring
    Box(
        modifier = modifier
            .size(64.dp)   // Premium size
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                        onClick()
                    }
                )
            }
    ) {

        // FAB core with heavy shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(4.dp)
                .background(
                    brush = Brush.linearGradient(
                        gradientColors
                    ),
                    shape = CircleShape
                )
        )

        // --- FAB Core ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(gradientColors),
                    shape = CircleShape
                )
                .shadow(
                    elevation = 14.dp,
                    shape = CircleShape,
                    ambientColor = gradientColors.first(),
                    spotColor = gradientColors.last()
                )
        ) {
            // --- Shimmer Layer ---
            Box(
                modifier = Modifier
                    .matchParentSize()
            )

            // Icon
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Entry",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(34.dp)
            )
        }
    }
}

/**
 * Empty-state UI shown when user has no entries.
 */
@Composable
fun EmptyJournalView(
    navigateToAddJournal: () -> Unit,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val gradientColors = listOf(
        if (isSystemInDarkTheme) AppBrand.gradientStartDark else AppBrand.gradientStartLight,
        if (isSystemInDarkTheme) AppBrand.gradientEndDark else AppBrand.gradientEndLight
    )

    Spacer(modifier = Modifier.height(24.dp))
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp)
            .background(
                if (isSystemInDarkTheme) AppBrand.emptyStateBackgroundDark else AppBrand.emptyStateBackgroundLight
            )
            .border(
                BorderStroke(
                    1.dp, if (isSystemInDarkTheme) AppBrand.emptyStateBorderDark else AppBrand.emptyStateBorderLight
                ), shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Icon(
            Icons.Filled.Edit,
            contentDescription = stringResource(R.string.newjournalentry),
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = stringResource(R.string.nojournalentries),
            fontSize = 22.sp
        )
        Text(
            text = stringResource(R.string.startjournaling),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // CTA button with gradient
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent // Set container color to transparent
            ),
            onClick = { navigateToAddJournal() },
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = gradientColors
                    ), shape = RoundedCornerShape(25.dp)
                ),
        ) {
            Text(
                text = stringResource(R.string.newjournalentry),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 0.dp)
            )
        }
    }
}

/**
 * Simple "Journal" title header.
 */
@Composable
private fun JournalHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        Text(
            text = "📝 " + stringResource(R.string.journal),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Search bar with clear button + built-in sort toggle.
 * Auto-updates ViewModel search state as user types.
 */
@Composable
private fun JournalSearchBar(
    viewModel: JournalViewModel,
) {
    val coachTextFieldValueState by viewModel.searchTextFieldValue.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sortNewestFirst by viewModel.sortNewestFirst.collectAsStateWithLifecycle()

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Main text field
        OutlinedTextField(
            value = coachTextFieldValueState,
            onValueChange = {
                viewModel.updateSearchTextFieldValue(it)
                if (it.text.isBlank()) {
                    viewModel.clearSearch()
                }
            },
            placeholder = {
                Text(
                    stringResource(R.string.searchentries),
                    fontSize = 18.sp
                )
            },
            trailingIcon = {
                if (isSearchActive) {
                    IconButton(onClick = {
                        viewModel.clearSearch()
                        keyboardController?.hide()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear search",
                            tint = Color.Gray
                        )
                    }
                } else {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        modifier = Modifier.clickable {
                            keyboardController?.hide()
                            if (coachTextFieldValueState.text.isNotBlank()) {
                                viewModel.searchJournals(coachTextFieldValueState.text)
                            }
                        }
                    )
                }
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
                .shadow(2.dp, RoundedCornerShape(50.dp))
                .focusRequester(focusRequester),
            textStyle = TextStyle(fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(50), // Adjust corner radius as needed
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.Gray,
                disabledIndicatorColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                keyboardController?.hide()
                if (coachTextFieldValueState.text.isNotBlank()) {
                    viewModel.searchJournals(coachTextFieldValueState.text)
                }
            }),
        )
        AnimatedVisibility(
            visible = !isSearchActive,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(200))
        ) {
            // Sort toggle (hidden while searching)
            IconButton(onClick = {
                viewModel.sortNewestFirst.value = !viewModel.sortNewestFirst.value
            }) {
                Icon(
                    imageVector = if (sortNewestFirst) Icons.Filled.South else Icons.Filled.North,
                    contentDescription = if (sortNewestFirst) "Sort Descending" else "Sort Ascending",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

/**
 * Mood filter dropdown
 */
@Composable
private fun JournalFilters(
    viewModel: JournalViewModel,
) {
    val selectedMoodValue by viewModel.selectedMood.collectAsStateWithLifecycle()
    var moodMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
//            .padding(top = 16.dp, bottom = 8.dp)
            .animateContentSize(
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
    ) {

        // ---- FILTERS HEADER ----
        Text(
            text = stringResource(R.string.filters),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // ---- MOOD PICKER ROW ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mood filter button
            Button(
                onClick = { moodMenuExpanded = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .height(44.dp)
                    .padding(top = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    // Larger emoji for premium feel
                    Text("😊", fontSize = 26.sp)

                    Text(
                        text = if (selectedMoodValue.isNotBlank())
                            Mood.fromRawValue(selectedMoodValue)?.emoji ?: ""
                        else stringResource(R.string.mood),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    Icon(
                        imageVector =
                            if (moodMenuExpanded) Icons.Filled.ArrowDropUp
                            else Icons.Filled.ArrowDropDown,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Optional future: add Goal filter here
        }

        // Dropdown content
        DropdownMenu(
            expanded = moodMenuExpanded,
            onDismissRequest = { moodMenuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.none)) },
                onClick = {
                    viewModel.selectedMood.value = ""
                    moodMenuExpanded = false
                }
            )
            Mood.entries.forEach { mood ->
                DropdownMenuItem(
                    text = { Text(mood.rawValue) },
                    onClick = {
                        viewModel.selectedMood.value = mood.rawValue
                        moodMenuExpanded = false
                    }
                )
            }
        }
    }
}

/**
 * Daily reflection prompt card.
 * Fully reusable for any template or app.
 */
@Composable
private fun ReflectionCard(viewModel: JournalViewModel) {
    val dailyJournalPrompt by viewModel.dailyJournalPrompt.collectAsStateWithLifecycle()

    BridgeBaseCard(
        backgroundColor = Color(0xFFE8F5E9),
    ) {

        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = stringResource(R.string.todaysreflection),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            if (dailyJournalPrompt.isNotEmpty()) {
                Text(
                    text = dailyJournalPrompt,
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF4A4A4A),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * Section label used above each grouped month.
 */
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

/**
 * Card for individual journal entry in the list.
 */
@Composable
private fun JournalEntryCard(
    entry: JournalEntry,
    onClick: () -> Unit
) {

    BridgeBaseCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = entry.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = entry.entry,
                fontSize = 15.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            TextButton(
                onClick = onClick,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .height(32.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.readmore),
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = Mood.fromRawValue(entry.mood)?.emoji ?: "",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(entry.date.toFormattedDateString(), fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
