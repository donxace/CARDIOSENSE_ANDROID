package com.example.arduino.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app.model.summary.WeeklyHealthScore
import com.example.arduino.model.summary.DailyHealthScore

@Dao
interface WeeklyHealthScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weeklyHealthScore: WeeklyHealthScore)

    @Query("SELECT * FROM weekly_health_scores WHERE weekStart = :weekStart")
    suspend fun getByWeek(weekStart: Long): WeeklyHealthScore?
}