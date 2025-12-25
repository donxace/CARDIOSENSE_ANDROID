package com.example.app.model.summary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_health_scores")
data class WeeklyHealthScore(
    @PrimaryKey val weekStart: Long,      // timestamp of the first day of the week
    val healthScore: Float,               // calculated from the 7 daily scores
    val totalSessions: Int? = null,       // optional: total sessions in the week
)