package com.bridgebase.bridgebase.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedDateString(): String {
    // Ensure the Long represents milliseconds. If it's seconds, multiply by 1000.
    val date = Date(this)
    // MMMM for full month name, d for day of month, yyyy for year.
    val format = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    return format.format(date)
}