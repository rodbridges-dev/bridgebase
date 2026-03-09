package com.bridgebase.bridgebase.data.journal

import com.bridgebase.bridgebase.common.FirebaseResult
import com.bridgebase.bridgebase.domain.ActivityItem
import com.bridgebase.bridgebase.domain.JournalEntry
import com.bridgebase.bridgebase.domain.JournalSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * FakeJournalRepository
 *
 * A mock implementation of [JournalRepository] used for template previews,
 * testing, and development without requiring a real backend.
 *
 * This class simulates:
 * - Network delay
 * - Realistic journal streak data
 * - A small activity feed
 *
 * It can be swapped out with a Firebase or REST-backed repository simply
 * by changing the Hilt binding in `RepositoryModule`.
 */
class FakeJournalRepository @Inject constructor() : JournalRepository {

    /**
     * Emits a complete [com.bridgebase.bridgebase.domain.JournalSummary] object after a simulated delay.
     *
     * Behaviors demonstrated:
     * - Fake network latency
     * - Computed `lastEntryDate` using the current timestamp
     * - A realistic set of recent activity entries
     *
     * In production, this would map Firestore/REST data into domain models.
     */
    override fun getJournalSummary(): Flow<FirebaseResult<JournalSummary>> = flow {
        emit(FirebaseResult.Loading)

        // Simulate network latency — good for showing loading states
        delay(500)  // simulate network latency

        try {
            val result = JournalSummary(
                hasJournalToday = false,
                streakCount = 2,
                lastEntryDate = System.currentTimeMillis(),
                message = "Build momentum by reflecting daily.",

                // Fake recent activity feed
                recentActivity = listOf(
                    ActivityItem("Journal Entry", "Yesterday • Feeling good"),
                    ActivityItem("Goal Progress", "2/5 steps completed"),
                    ActivityItem("Chat Message", "Reply from Coach")
                )
            )
            emit(FirebaseResult.Success(result))
        } catch (e: Exception) {
            emit(FirebaseResult.Error("Failed to load fake goals", e))
        }

    }

    private fun randomPastDate(daysBack: IntRange): Long {
        val now = System.currentTimeMillis()
        val days = daysBack.random()
        return now - days * 24L * 60L * 60L * 1000L
    }

    override fun getJournalEntries(): Flow<FirebaseResult<List<JournalEntry>>> = flow {
        emit(FirebaseResult.Loading)

        delay(500) // simulate network latency

        try {
            val result = listOf(
                JournalEntry(
                    id = "1",
                    title = "A Quiet Start",
                    entry = "I woke up feeling surprisingly peaceful today. Instead of diving straight into tasks, I took a moment to sit by the window and enjoy the morning light. It gave me a sense of calm I didn’t realize I needed. As the day went on, that gentle stillness stayed with me, helping me move through my schedule without feeling rushed or overwhelmed. I’m trying to make more space for these quiet, grounding moments—they seem to set the tone for everything that follows.",
                    date = randomPastDate(5..30),
                    mood = "Calm"
                ),
                JournalEntry(
                    id = "2",
                    title = "Building Momentum",
                    entry = "Today was one of those days where I had to push myself to get started. Once I finally sat down and committed to just a few minutes of work, something clicked. My energy shifted, and I found myself slipping into a productive rhythm. I still struggled with a few things, but each small win made the next step feel a little easier. It reminded me that momentum isn’t something I wait for—it’s something I build.",
                    date = randomPastDate(10..45),
                    mood = "Motivated"
                ),
                JournalEntry(
                    id = "3",
                    title = "Edges of Worry",
                    entry = "I felt on edge today, like my thoughts were buzzing just under the surface. Nothing was wrong exactly, but everything felt heightened—sounds, conversations, even small decisions. I tried grounding exercises and took a few breaks, which helped a little. Even though the anxious feeling didn’t fully go away, I’m proud that I didn’t let it control my whole day. I showed up as best as I could, even with the heaviness.",
                    date = randomPastDate(20..60),
                    mood = "Anxious"
                ),
                JournalEntry(
                    id = "4",
                    title = "Noticing the Good",
                    entry = "During lunch I stepped outside and really paid attention to the world around me—the warmth of the sun, people laughing nearby, the smell of food from a café. It was simple, but it lifted my mood in a way I didn’t expect. I often rush through the day without slowing down long enough to appreciate anything. Today reminded me that even when things are stressful, there are still good moments everywhere if I pause long enough to notice them.",
                    date = randomPastDate(30..75),
                    mood = "Grateful"
                ),
                JournalEntry(
                    id = "5",
                    title = "Weighed Down",
                    entry = "I spent most of the afternoon feeling overwhelmed by how much I needed to get done. Every task felt bigger than it actually was, and I kept second-guessing myself. Even though it wasn’t my most productive day, I tried to stay patient with myself. I took a few short breaks, drank some water, and did what I could without burning out. I’m slowly learning that being stressed doesn’t mean I’m failing—it means I need to be gentler with myself.",
                    date = randomPastDate(45..90),
                    mood = "Stressed"
                ),
                JournalEntry(
                    id = "6",
                    title = "Midnight Reset",
                    entry = "I couldn’t sleep tonight, so I opened my journal and let my thoughts spill out. Once I started writing, I realized how much I had been holding in—little frustrations, unanswered questions, and the feeling that I wasn’t doing enough. Seeing it all written down helped untangle it. I ended up feeling lighter, like I gave myself permission to reset emotionally. Maybe tomorrow will feel a little clearer.",
                    date = randomPastDate(60..120),
                    mood = "Reflective"
                ),
            )
            emit(FirebaseResult.Success(result))
        } catch (e: Exception) {
            emit(FirebaseResult.Error("Failed to load fake goals", e))
        }
    }
}