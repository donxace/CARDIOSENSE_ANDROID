package com.example.arduino.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SessionMetricsDao {

    @Query("SELECT * FROM session_metrics ORDER BY sessionStartTime DESC")
    suspend fun getAllMetrics(): List<SessionMetricsEntity>

    @Query("SELECT * FROM session_metrics WHERE sessionStartTime >= :since ORDER BY sessionStartTime DESC")
    suspend fun getSessionsSince(since: Long): List<SessionMetricsEntity>

    // 3️⃣ Get sessions for a specific day using sessionId (timestamp)
    @Query("""
        SELECT * FROM session_metrics
        WHERE sessionId BETWEEN :startOfDay AND :endOfDay
        ORDER BY sessionId ASC
    """)
    suspend fun getSessionsByDay(startOfDay: Long, endOfDay: Long): List<SessionMetricsEntity>

    @Query("SELECT * FROM session_metrics WHERE sessionStartTime = :dayId ORDER BY sessionStartTime ASC")
    suspend fun getSessionsByDayId(dayId: Int): List<SessionMetricsEntity>

    // Flexible range query: get all sessions between start and end timestamps
    @Query("SELECT * FROM session_metrics WHERE sessionId BETWEEN :start AND :end ORDER BY sessionStartTime DESC")
    suspend fun getSessionsBetween(start: Long, end: Long): List<SessionMetricsEntity>

        // Convenience function for last N hours
        suspend fun getLastNHours(hours: Int): List<SessionMetricsEntity> {
            val end = System.currentTimeMillis()
            val start = end - hours * 60 * 60 * 1000
            return getSessionsBetween(start, end)
        }

        // Convenience function for last N days
        suspend fun getLastNDays(days: Int): List<SessionMetricsEntity> {
            val end = System.currentTimeMillis()
            val start = end - days * 24 * 60 * 60 * 1000
            return getSessionsBetween(start, end)
        }

        // Convenience function for last N years
        suspend fun getLastNYears(years: Int): List<SessionMetricsEntity> {
            val end = System.currentTimeMillis()
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = end
            calendar.add(java.util.Calendar.YEAR, -years)
            val start = calendar.timeInMillis
            return getSessionsBetween(start, end)
        }
}
