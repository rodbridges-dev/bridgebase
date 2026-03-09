package com.bridgebase.bridgebase.common.enums

enum class Mood(val rawValue: String, val emoji: String) {
    HAPPY("Happy", "\uD83D\uDE0A"),
    SAD("Sad", "\uD83D\uDE22"),
    STRESSED("Stressed", "\uD83D\uDE23"),
    CALM("Calm", "\uD83D\uDE0C"),
    ANGRY("Angry", "\uD83D\uDE20"),
    MOTIVATED("Motivated", "\uD83D\uDCAA"),
    ANXIOUS("Anxious", "\uD83D\uDE30"),
    TIRED("Tired", "\uD83D\uDE34"),
    GRATEFUL("Grateful", "\uD83D\uDE4F"),
    REFLECTIVE("Reflective", "\uD83E\uDD14");

    companion object {
        fun fromRawValue(value: String): Mood? = entries.find { it.rawValue == value }
    }
}