package com.bridgebase.bridgebase.ui.components

import java.util.Calendar

fun getJournalEmoji(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5 until 12 -> "🌅" // Morning (5 AM to 11:59 AM)
        in 12 until 17 -> "📝" // Afternoon (12 PM to 4:59 PM)
        in 17 until 21 -> "🌇" // Evening (5 PM to 8:59 PM)
        else -> "🌙" // Night (9 PM to 4:59 AM)
    }
}