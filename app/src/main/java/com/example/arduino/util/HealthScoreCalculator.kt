package com.example.arduino.util

import com.example.app.model.summary.WeeklyHealthScore
import com.example.arduino.data.SessionMetricsDao
import com.example.arduino.data.dao.DailyHealthScoreDao
import com.example.arduino.data.dao.WeeklyHealthScoreDao
import com.example.arduino.model.summary.DailyHealthScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

import kotlin.math.pow
import kotlin.math.sqrt

suspend fun calculateDailyHealthScore(
    daoSession: SessionMetricsDao,
    daoDaily: DailyHealthScoreDao
) {
    // --- Compute MMDDYY for today ---
    val cal = Calendar.getInstance()
    val month = cal.get(Calendar.MONTH) + 1  // January = 0
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val year = cal.get(Calendar.YEAR) % 100  // last two digits
    val mmddyy = (month * 10000 + day * 100 + year).toLong() // e.g., 122525

    // Compute start and end of day in millis for querying sessions
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val startOfDayMillis = cal.timeInMillis
    val endOfDayMillis = startOfDayMillis + 24 * 60 * 60 * 1000 - 1

    // Get all sessions for today
    val sessions = daoSession.getSessionsBetween(startOfDayMillis, endOfDayMillis)

    if (sessions.isNotEmpty()) {
        val sessionScores = sessions.map { record ->
            val bpm = record.bpm
            val hrv = record.rmssd // RMSSD already stored

            // BPM scoring
            val bpmScore = when {
                bpm < 50f -> 100f
                bpm < 60f -> 90f + (50f - bpm) * 1f
                bpm < 70f -> 80f + (60f - bpm) * 1f
                bpm < 80f -> 50f + (70f - bpm) * 0.5f
                else -> ((100f - bpm) / 20f * 50f).coerceIn(0f, 50f)
            }.coerceIn(0f, 100f)

            // HRV scoring
            val hrvScore = when {
                hrv < 19f -> 0f
                hrv < 50f -> ((hrv - 19f) / (50f - 19f) * 80f).coerceIn(0f, 80f)
                hrv <= 75f -> 80f + ((hrv - 50f) / (75f - 50f) * 20f)
                else -> 100f
            }

            ((bpmScore + hrvScore) / 2).coerceIn(0f, 100f)
        }

        val healthScore = sessionScores.average().toFloat()

        // Check if a daily record already exists
        val existingDaily = daoDaily.getByDate(mmddyy)

        val dailyScore = DailyHealthScore(
            date = mmddyy, // MMDDYY as key
            healthScore = healthScore,
            totalSessions = sessions.size
        )

        // Insert or update (REPLACE strategy in DAO)
        daoDaily.insert(dailyScore)
    }
}

suspend fun calculateWeeklyHealthScore(
    daoDaily: DailyHealthScoreDao,
    daoWeekly: WeeklyHealthScoreDao,
    weekStart: Long
) {
    val weekEnd = weekStart + 7 * 24 * 60 * 60 * 1000 - 1
    val dailyScores = daoDaily.getDailyScoresBetween(weekStart, weekEnd)

    if (dailyScores.isNotEmpty()) {
        val avgScore = dailyScores.map { it.healthScore }.average().toFloat()
        val totalSessions = dailyScores.sumOf { it.totalSessions ?: 0 }


        val weeklyScore = WeeklyHealthScore(
            weekStart = weekStart,
            healthScore = avgScore,
            totalSessions = totalSessions,
        )

        daoWeekly.insert(weeklyScore)
    }
}

fun startOfDay(timestamp: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = timestamp
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

fun weekStart(timestamp: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = timestamp
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek) // Sunday or Monday depending on locale
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}
