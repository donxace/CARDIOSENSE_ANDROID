package com.example.arduino.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SessionMetricsDao {

    @Query("SELECT * FROM session_metrics ORDER BY sessionStartTime DESC")
    suspend fun getAllMetrics(): List<SessionMetricsEntity>

    @Query("SELECT * FROM session_metrics WHERE sessionStartTime >= :since ORDER BY sessionStartTime DESC")
    suspend fun getSessionsSince(since: Long): List<SessionMetricsEntity>
}
