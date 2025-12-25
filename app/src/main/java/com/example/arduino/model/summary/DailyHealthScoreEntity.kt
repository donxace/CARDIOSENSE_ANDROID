package com.example.arduino.model.summary

import androidx.room.Entity
import androidx.room.PrimaryKey

// Daily
@Entity(tableName = "daily_health_scores")
data class DailyHealthScore(
    @PrimaryKey val date: String,    // start-of-day timestamp
    val healthScore: Float,
    val totalSessions: Int? = null
)
