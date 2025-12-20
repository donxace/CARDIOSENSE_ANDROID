package com.example.arduino.data

import android.util.Log
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun getStartAndEndOfSpecificDay(year: Int, month: Int, day: Int): Pair<Long, Long> {
    val cal = Calendar.getInstance()

    // Month is 0-based in Calendar (0 = January, 11 = December)
    cal.set(year, month - 1, day, 0, 0, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val startOfDay = cal.timeInMillis

    cal.set(year, month - 1, day, 23, 59, 59)
    cal.set(Calendar.MILLISECOND, 999)
    val endOfDay = cal.timeInMillis

    return Pair(startOfDay, endOfDay)
}

fun formatSessionTime(sessionId: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(sessionId))
}

fun generateDaySessionId(date: Date = Date()): Long {
    // Format date as MMddyy
    val sdf = SimpleDateFormat("MMddyy", Locale.getDefault())
    val dayString = sdf.format(date)
    return dayString.toLong()  // e.g., Dec 19, 2025 -> 121925

}

fun getTimeOfDayMessage(sessionId: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = sessionId
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

    return when (hourOfDay) {
        in 0..11 -> "MORNING TEST"
        in 12..17 -> "AFTERNOON TEST"
        else -> "EVENING TEST"
    }
}
