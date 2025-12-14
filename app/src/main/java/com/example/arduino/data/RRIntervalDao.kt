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
}