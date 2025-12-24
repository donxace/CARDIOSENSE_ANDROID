package com.example.arduino.ui.timedomain

import androidx.lifecycle.ViewModel
import com.example.arduino.data.SessionMetricsEntity
import com.example.arduino.data.repository.SessionRepository
import com.example.arduino.domain.SessionAverages

class TimeDomainViewModel(
    private val repository: SessionRepository
) : ViewModel() {

    suspend fun last24HoursAverages(): SessionAverages =
        calculateAverages(repository.lastHours(24))

    suspend fun last14DaysAverages(): SessionAverages =
        calculateAverages(repository.lastDays(14))

    suspend fun last30DaysAverages(): SessionAverages =
        calculateAverages(repository.lastDays(30))

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
