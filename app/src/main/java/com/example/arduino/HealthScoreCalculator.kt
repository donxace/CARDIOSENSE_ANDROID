package com.example.arduino

import android.util.Log
import kotlin.math.pow
import kotlin.math.sqrt

object HealthScoreCalculator {

    /**
     * Calculate health score for a single ActivityRecord.
     * Uses BPM (60-100) and RMSSD HRV (19-75 ms).
     * Returns a score from 0 to 100.
     */
    fun calculate(record: ActivityRecord): Float {
        val bpm = record.bpm

        // --- Compute RMSSD from RR intervals if not precomputed ---
        val hrv = if (record.rrData.size >= 2) {
            val diffSquared = record.rrData.zipWithNext { a, b -> (b - a).pow(2) }
            sqrt(diffSquared.average().toFloat())
        } else 0f

        // --- Realistic BPM scoring ---
        val bpmScore = when {
            bpm < 50f -> 100f
            bpm < 60f -> 90f + (50f - bpm) * 1f
            bpm < 70f -> 80f + (60f - bpm) * 1f
            bpm < 80f -> 50f + (70f - bpm) * 0.5f
            else -> ((100f - bpm) / 20f * 50f).coerceIn(0f, 50f)
        }.coerceIn(0f, 100f)

        // --- Realistic HRV (RMSSD) scoring ---
        val hrvScore = when {
            hrv < 19f -> 0f
            hrv < 50f -> ((hrv - 19f) / (50f - 19f) * 80f).coerceIn(0f, 80f)
            hrv <= 75f -> 80f + ((hrv - 50f) / (75f - 50f) * 20f)
            else -> 100f
        }

        val finalScore = (bpmScore * 0.4f + hrvScore * 0.6f).coerceIn(0f, 100f)

        // --- LOG SINGLE RECORD PROCESSING ---
        Log.d("HealthScore", """
            =====================================
            Processing Record:
            - Time: ${record.time}
            - DateTime: ${record.dateTime}
            - BPM: $bpm
            - RR Interval: ${record.rrInterval}
            - RR Data Size: ${record.rrData.size}
            
            Computed Values:
            - HRV (RMSSD): $hrv
            - BPM Score: $bpmScore
            - HRV Score: $hrvScore
            
            Final Health Score: $finalScore
            =====================================
        """.trimIndent())

        return finalScore
    }

    /**
     * Calculate a single health score for multiple ActivityRecords.
     * Returns a single Float score (0-100) representing overall health.
     */
    fun calculate(records: List<ActivityRecord>): Float {
        if (records.isEmpty()) {
            Log.d("HealthScore", "No records to process")
            return 0f
        }

        Log.d("HealthScore", "=========================================")
        Log.d("HealthScore", "Processing ${records.size} records")
        Log.d("HealthScore", "=========================================")

        val scores = records.mapIndexed { index, record ->
            val score = calculate(record)  // calls the single record function above
            Log.d("HealthScore", "Record ${index + 1}/${records.size} → Score: $score")
            score
        }

        val avgScore = scores.average().toFloat()

        Log.d("HealthScore", """
            =========================================
            Final Average Health Score: $avgScore
            Individual Scores: $scores
            =========================================
        """.trimIndent())

        return avgScore
    }

    /**
     * Smart analyzer: handles single record or list of records
     */
    fun analyze(input: Any): Any {
        return when (input) {
            is ActivityRecord -> calculate(input)
            is List<*> -> {
                val list = input.filterIsInstance<ActivityRecord>()
                calculate(list)
            }
            else -> 0f
        }
    }
}