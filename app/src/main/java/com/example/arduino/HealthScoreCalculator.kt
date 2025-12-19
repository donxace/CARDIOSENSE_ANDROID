package com.example.arduino

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
            bpm < 60f -> 90f + (50f - bpm) * 1f   // interpolate slightly
            bpm < 70f -> 80f + (60f - bpm) * 1f
            bpm < 80f -> 50f + (70f - bpm) * 0.5f
            else -> ((100f - bpm) / 20f * 50f).coerceIn(0f, 50f) // above 80
        }.coerceIn(0f, 100f)

        // --- Realistic HRV (RMSSD) scoring ---
        val hrvScore = when {
            hrv < 19f -> 0f
            hrv < 50f -> ((hrv - 19f) / (50f - 19f) * 80f).coerceIn(0f, 80f)
            hrv <= 75f -> 80f + ((hrv - 50f) / (75f - 50f) * 20f)  // interpolate to 100
            else -> 100f
        }

        // --- Weighted average: 40% BPM, 60% HRV ---
        return (bpmScore * 0.4f + hrvScore * 0.6f).coerceIn(0f, 100f)
    }

    /**
     * Calculate a single health score for multiple ActivityRecords.
     * Returns a single Float score (0-100) representing overall health.
     */
    fun calculate(records: List<ActivityRecord>): Float {
        if (records.isEmpty()) return 0f

        val scores = records.map { calculate(it) }  // calculate each record
        return scores.average().toFloat()           // average all scores
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