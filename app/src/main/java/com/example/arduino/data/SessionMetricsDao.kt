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

}
