package com.example.arduino.data

import android.util.Log
import java.util.Calendar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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


fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
    return formatter.format(date)
}

fun incrementDate(currentDate: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = currentDate
    calendar.add(Calendar.DAY_OF_MONTH, 1)
    return calendar.time
}

fun decrementDate(currentDate: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = currentDate
    calendar.add(Calendar.DAY_OF_MONTH, -1)
    return calendar.time
}

fun formatDateToMMDDYY(date: Date): String {
    val calendar = Calendar.getInstance()
    calendar.time = date
    val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
    val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
    val year = (calendar.get(Calendar.YEAR) % 100).toString().padStart(2, '0')
    return "$month$day$year"
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

fun mmddyyLongToDate(mmddyy: Long): Date? {
    val sdf = SimpleDateFormat("MMddyy", Locale.getDefault())
    return try {
        sdf.parse(mmddyy.toString().padStart(6, '0')) // ensures leading zeros
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun groupSessionsByWeek(sessions: List<SessionMetricsEntity>): Map<Int, List<SessionMetricsEntity>> {
    val calendar = Calendar.getInstance()
    return sessions.groupBy { session ->
        calendar.timeInMillis = session.sessionId
        calendar.get(Calendar.WEEK_OF_YEAR)  // returns 1..53
    }
}

fun groupSessionsByDay(sessions: List<SessionMetricsEntity>): Map<String, List<SessionMetricsEntity>> {
    val calendar = Calendar.getInstance()
    return sessions.groupBy { session ->
        calendar.timeInMillis = session.sessionId
        // Format as "YYYY-MM-DD"
        "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
    }
}


