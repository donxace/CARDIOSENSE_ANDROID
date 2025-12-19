package com.example.arduino.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RRIntervalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rrIntervals: List<RRInterval>)

    @Query("SELECT * FROM rr_intervals WHERE sessionId = :sessionId")
    suspend fun getAllBySession(sessionId: Long): List<RRInterval>

    // --- Insert session metrics ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetrics(metrics: SessionMetricsEntity)

    // --- Get metrics for all sessions ---
    @Query("SELECT * FROM session_metrics ORDER BY sessionStartTime DESC")
    suspend fun getAllMetrics(): List<SessionMetricsEntity>

    // --- Get metrics after a certain timestamp (last 7 days) ---
    @Query("SELECT * FROM session_metrics WHERE sessionStartTime >= :timestamp ORDER BY sessionStartTime DESC")
    suspend fun getMetricsAfter(timestamp: Long): List<SessionMetricsEntity>

    // --- Optional: Delete old sessions (cleanup) ---
    @Query("DELETE FROM session_metrics WHERE sessionStartTime < :timestamp")
    suspend fun deleteOldSessions(timestamp: Long)

    @Query("SELECT * FROM session_metrics WHERE sessionStartTime = :dayId ORDER BY sessionStartTime ASC")
    suspend fun getSessionsByDayId(dayId: Int): List<SessionMetricsEntity>


    // ✅ Get ONLY rrValue for a given sessionId
    @Query("""
        SELECT rrValue 
        FROM rr_intervals 
        WHERE timestamp = :sessionId
        ORDER BY sessionStartTime ASC
    """)
    suspend fun getRRValuesForSession(sessionId: Long): List<Float>

    @Query("""
        SELECT *
        FROM rr_intervals
        ORDER BY sessionId ASC, timestamp ASC
    """)
    suspend fun getAllRRIntervalsOrdered(): List<RRInterval>
}


