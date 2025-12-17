package com.example.arduino.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SessionMetricsEntity(
    @PrimaryKey val sessionId: Long,
    val avgRR: Float,
    val bpm: Float,
    val sdnn: Float,
    val sessionStartTime: Long
)