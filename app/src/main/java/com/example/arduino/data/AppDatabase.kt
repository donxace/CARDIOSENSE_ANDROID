package com.example.arduino.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.app.model.summary.WeeklyHealthScore
import com.example.arduino.data.dao.DailyHealthScoreDao
import com.example.arduino.data.dao.WeeklyHealthScoreDao
import com.example.arduino.model.summary.DailyHealthScore

@Database(entities = [RRInterval::class, SessionMetricsEntity::class, WeeklyHealthScore::class, DailyHealthScore::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rrIntervalDao(): RRIntervalDao
    abstract fun sessionMetricsDao(): SessionMetricsDao

    abstract fun dailyHealthScoreDao(): DailyHealthScoreDao
    abstract fun weeklyHealthScoreDao(): WeeklyHealthScoreDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rr_interval_db"
                )
                    .fallbackToDestructiveMigration() // handles schema changes safely for dev
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

