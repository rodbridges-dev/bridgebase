package com.bridgebase.bridgebase.ui.journal

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridgebase.bridgebase.common.FirebaseResult
import com.bridgebase.bridgebase.common.enums.Mood
import com.bridgebase.bridgebase.data.journal.JournalRepository
import com.bridgebase.bridgebase.domain.JournalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for all Journal-related UI.
 *
 * Handles:
 * - Loading entries
 * - Searching / filtering / sorting
 * - Providing daily prompt
 * - Adding new entries
 *
 * This class cleanly separates UI from data sources,
 * making it fully interchangeable for template buyers.
 */
@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    // ------------------------------------------------------------
    // UI STATE: SEARCH
    // ------------------------------------------------------------

    /** Value of the search bar text field. */
    private val _searchTextFieldValue = MutableStateFlow(TextFieldValue(""))
    val searchTextFieldValue = _searchTextFieldValue.asStateFlow()

    /** Whether the UI is currently showing active search results. */
    val isSearchActive = MutableStateFlow(false)

    /** List of search-matched entries. */
    val searchResults = MutableStateFlow<List<JournalEntry>>(emptyList())

    /** Track last executed query (optional for analytics / debounce). */
    var lastSearchQuery: String = ""

    // ------------------------------------------------------------
    // UI STATE: FILTERS & SORTING
    // ------------------------------------------------------------

    /** Selected mood filter. Empty string = no filter. */
    val selectedMood = MutableStateFlow("")

    /** Sort toggle (true = newest first). */
    val sortNewestFirst = MutableStateFlow(true)

    /** Daily reflection prompt (stubbed for template). */
    val dailyJournalPrompt = MutableStateFlow(
        "What’s one small thing you can do today to move forward?"
    )

    // ------------------------------------------------------------
    // JOURNAL ENTRY DATA
    // ------------------------------------------------------------

    /** Internal list of all entries loaded from repository. */
    private val _entries = MutableStateFlow(emptyList<JournalEntry>())

    /** Public flow exposed to UI. */
    val entries = _entries.asStateFlow()

    /** Indicates whether the initial load is still running. */
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    /**
     * Combine entries + mood filter + sort order into a
     * single Flow exposed to the UI.
     */
    val filteredEntries = combine(
        entries,
        selectedMood,
        sortNewestFirst
    ) { allEntries, mood, newestFirst ->

        allEntries
            // Mood filter
            .filter { entry ->
                mood.isBlank() || entry.mood == mood
            }
            // Always sort by date first
            .sortedBy { it.date }
            // Reverse if newest-first is enabled
            .let { if (newestFirst) it.reversed() else it }

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    // ------------------------------------------------------------
    // INITIAL LOAD
    // ------------------------------------------------------------

    init {
        viewModelScope.launch { getJournalEntries() }
    }

    /**
     * Loads all journal entries from the repository.
     * Repository is DI-injected and interchangeable by buyers.
     */
    suspend fun getJournalEntries() {
        journalRepository.getJournalEntries().collect { result ->

            when (result) {

                is FirebaseResult.Loading -> {
                    _isLoading.value = true
                }

                is FirebaseResult.Success -> {
                    val entries = result.data
                    _entries.value = entries
                    _isLoading.value = false
                }

                is FirebaseResult.Error -> {
                    _entries.value = emptyList()
                    _isLoading.value = false
                }
            }
        }
    }

    // ------------------------------------------------------------
    // GROUPING HELPERS
    // ------------------------------------------------------------

    /**
     * Convert timestamps into "Month YYYY" groups.
     */
    fun groupEntriesByMonthYear(entries: List<JournalEntry>): Map<String, List<JournalEntry>> {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

        return entries.groupBy { entry ->
            val date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(entry.date),
                ZoneId.systemDefault()
            )
            date.format(formatter)
        }
    }

    // ------------------------------------------------------------
    // SEARCH LOGIC
    // ------------------------------------------------------------

    fun updateSearchTextFieldValue(newValue: TextFieldValue) {
        _searchTextFieldValue.value = newValue
    }

    fun clearSearchTextFieldValue() {
        _searchTextFieldValue.value = TextFieldValue("")
    }

    /**
     * Reset search state entirely.
     */
    fun clearSearch() {
        searchResults.value = emptyList()
        clearSearchTextFieldValue()
        isSearchActive.value = false
        lastSearchQuery = ""
    }

    /**
     * Basic search implementation:
     * Matches if text appears in title OR body.
     */
    fun searchJournals(searchText: String) {
        viewModelScope.launch {
            selectedMood.value = "" // Remove mood filter during search

            val query = searchText.trim()
            isSearchActive.value = query.isNotEmpty()
            lastSearchQuery = query

            if (query.isBlank()) {
                searchResults.value = emptyList()
                return@launch
            }

            val all = entries.value

            val results = all.filter { entry ->
                entry.title.contains(query, ignoreCase = true) ||
                        entry.entry.contains(query, ignoreCase = true)
            }

            searchResults.value = results
        }
    }

    // ------------------------------------------------------------
    // ENTRY CREATION
    // ------------------------------------------------------------

    /**
     * Add a new journal entry to the local list.
     * (Template buyers can replace this with real backend storage.)
     */
    fun addEntry(title: String, content: String, mood: Mood?) {
        val newEntry = JournalEntry(
            id = UUID.randomUUID().toString(),
            title = title,
            entry = content,
            date = System.currentTimeMillis(),
            mood = mood?.rawValue ?: ""
        )

        // Prepend new entry at the top
        _entries.value = listOf(newEntry) + _entries.value

    }
}