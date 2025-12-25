package com.example.arduino.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arduino.model.summary.DailyHealthScore

@Dao
interface DailyHealthScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyHealthScore: DailyHealthScore)

    @Query("SELECT * FROM daily_health_scores ORDER BY date ASC")
    suspend fun getAll(): List<DailyHealthScore>

    @Query("SELECT * FROM daily_health_scores WHERE date = :date")
    suspend fun getByDate(date: Long): DailyHealthScore?

    @Query("SELECT * FROM daily_health_scores WHERE date BETWEEN :start AND :end")
    suspend fun getDailyScoresBetween(start: Long, end: Long): List<DailyHealthScore>

    @Query("SELECT * FROM daily_health_scores WHERE date = :selectedDate")
    suspend fun getDailyScoreByDate(selectedDate: Long): DailyHealthScore?


    @Query("SELECT * FROM daily_health_scores WHERE date BETWEEN :weekStart AND :weekEnd ORDER BY date ASC")
    suspend fun getWeekScores(weekStart: String, weekEnd: String): List<DailyHealthScore>

    @Query("SELECT * FROM daily_health_scores WHERE date = :date LIMIT 1")
    suspend fun getScoreByDate(date: String): DailyHealthScore?

}