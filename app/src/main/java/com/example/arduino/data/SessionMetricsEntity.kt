package com.example.arduino.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_metrics")
data class SessionMetricsEntity(
    @PrimaryKey val sessionId: Long,
    val sessionStartTime: Long,
    val bpm: Float,
    val sdnn: Float,
    val rmssd: Float,
    val nn50: Int,
    val pnn50: Float,
    val avgRR: Float
)

