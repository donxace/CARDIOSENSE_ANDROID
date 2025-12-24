package com.example.arduino.data.repository

import com.example.arduino.data.SessionMetricsDao
import com.example.arduino.data.SessionMetricsEntity

class SessionRepository(private val dao: SessionMetricsDao) {

    // Get last N hours
    suspend fun lastHours(hours: Int): List<SessionMetricsEntity> {
        return dao.getLastNHours(hours)
    }

    // Get last N days
    suspend fun lastDays(days: Int): List<SessionMetricsEntity> {
        return dao.getLastNDays(days)
    }

    // You could add other methods if needed, e.g., last N years
    suspend fun lastYears(years: Int): List<SessionMetricsEntity> {
        return dao.getLastNYears(years)
    }
}
