package com.example.arduino.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rr_intervals")
data class RRInterval(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val sessionStartTime: Long,
    val timestamp: Long,
    val rrValue: Float
)