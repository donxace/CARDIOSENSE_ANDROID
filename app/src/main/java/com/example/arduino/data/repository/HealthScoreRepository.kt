package com.example.arduino.data.repository

import android.util.Log
import com.example.arduino.data.dao.DailyHealthScoreDao
import com.example.arduino.model.summary.DailyHealthScore
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HealthScoreRepository(
    private val daoDaily: DailyHealthScoreDao
) {

    companion object {
        private const val TAG = "HealthScoreRepository"
    }

    suspend fun prefillWeekDailyHealthScores() {
        withContext(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            calendar.firstDayOfWeek = Calendar.MONDAY
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) // Monday of this week

            val sdfValue = SimpleDateFormat("MMddyy", Locale.US)
            val sdfDisplay = SimpleDateFormat("EEEE", Locale.US)

            for (i in 0..4) { // Monday → Friday
                val dateValue = sdfValue.format(calendar.time).toLong() // e.g., 122625
                val dayName = sdfDisplay.format(calendar.time)

                val dailyScore = DailyHealthScore(
                    date = dateValue.toString(),
                    healthScore = 0f,    // placeholder
                    totalSessions = 0,

                )

                daoDaily.insert(dailyScore)

                // Log inserted data
                Log.d(TAG, "Inserted placeholder for $dayName: $dateValue")
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }


}
