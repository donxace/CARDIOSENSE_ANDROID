package com.example.arduino.ui.timedomain

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.arduino.data.AppDatabase
import com.example.arduino.data.SessionMetricsEntity
import com.example.arduino.data.repository.SessionRepository
import com.example.arduino.domain.SessionAverages
import com.example.arduino.data.groupSessionsByDay
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class TimeDomainViewModel(
    private val repository: SessionRepository
) : ViewModel() {


    suspend fun logAndGetDailyAverages(days: Int): List<SessionAverages> {
        val sessions = repository.lastDays(days)  // <-- single fetch

        // Remove duplicate session IDs just in case
        val uniqueSessions = sessions.distinctBy { it.sessionId }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfFull = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // Debug: log all sessions fetched
        uniqueSessions.forEach { session ->
            Log.d("CheckSessions", "SessionID: ${session.sessionId} -> ${sdfFull.format(Date(session.sessionId))}")
        }

        // Group sessions by date
        val groupedByDay = uniqueSessions.groupBy {
            sdf.format(Date(it.sessionId)) // makes it daily based only on date
        }

        val dailyAverages = groupedByDay.map { (day, list) ->

            val avgBPM = list.map { it.bpm }.average().toFloat()
            val avgRR = list.map { it.avgRR }.average().toFloat()      // <-- FIXED
            val avgRMSSD = list.map { it.rmssd }.average().toFloat()
            val avgSDNN = list.map { it.sdnn }.average().toFloat()
            val avgNN50 = list.map { it.nn50 }.average().toInt()
            val avgPNN50 = list.map { it.pnn50 }.average().toFloat()

            Log.d("DailyAverages",
                "Date: $day | BPM:$avgBPM | RR:$avgRR | RMSSD:$avgRMSSD | SDNN:$avgSDNN | NN50:$avgNN50 | PNN50:$avgPNN50"
            )

            SessionAverages(avgBPM, avgSDNN, avgRMSSD, avgNN50, avgPNN50, avgRR)
        }

        return dailyAverages
    }

    suspend fun last24HoursAverages(): SessionAverages =
        calculateAverages(repository.lastHours(24))

    suspend fun last14DaysAverages(): SessionAverages =
        calculateAverages(repository.lastDays(14))

    suspend fun last30DaysAverages(): SessionAverages =
        calculateAverages(repository.lastDays(30))

    // ---------------- Raw Metrics Lists ----------------
    suspend fun last24HoursMetrics(): List<SessionMetricsEntity> =
        repository.lastHours(24)

    suspend fun last14DaysMetrics(): List<SessionMetricsEntity> =
        repository.lastDays(14)

    suspend fun last30DaysMetrics(): List<SessionMetricsEntity> =
        repository.lastDays(14 )


    fun calculateAverages(sessions: List<SessionMetricsEntity>): SessionAverages {
        if (sessions.isEmpty()) {
            return SessionAverages(
                avgBPM = 0f,
                avgSDNN = 0f,
                avgRMSSD = 0f,
                avgNN50 = 0,
                avgPNN50 = 0f,
                avgRR = 0f
            )
        }

        var bpmSum = 0f
        var sdnnSum = 0f
        var rmssdSum = 0f
        var nn50Sum = 0
        var pnn50Sum = 0f
        var rrSum = 0f

        sessions.forEach { session ->
            bpmSum += session.bpm
            sdnnSum += session.sdnn
            rmssdSum += session.rmssd
            nn50Sum += session.nn50
            pnn50Sum += session.pnn50
            rrSum += session.avgRR
        }

        return SessionAverages(
            avgBPM = bpmSum / sessions.size,
            avgSDNN = sdnnSum / sessions.size,
            avgRMSSD = rmssdSum / sessions.size,
            avgNN50 = nn50Sum / sessions.size,
            avgPNN50 = pnn50Sum / sessions.size,
            avgRR = rrSum / sessions.size
        )
    }
}




