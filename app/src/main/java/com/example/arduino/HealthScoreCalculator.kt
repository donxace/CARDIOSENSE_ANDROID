package com.example.arduino

object HealthScoreCalculator {
    fun calculate(
        bpm: Float,
        averageRR: Float,
        sdnn: Float,
        rmssd: Float,
        nn50: Int,
        pnn50: Float
    ): Float {
        // Normalize each metric to a 0-100 scale (example ranges)
        val bpmScore = ((bpm - 50) / (120 - 50) * 100).coerceIn(0f, 100f)
        val rrScore = (averageRR / 1000f * 100f).coerceIn(0f, 100f)
        val sdnnScore = (sdnn / 150f * 100f).coerceIn(0f, 100f)
        val rmssdScore = (rmssd / 200f * 100f).coerceIn(0f, 100f)
        val nn50Score = (nn50 / 50f * 100f).coerceIn(0f, 100f)
        val pnn50Score = pnn50.coerceIn(0f, 100f)

        // Weighted sum
        val score = (bpmScore + rrScore + sdnnScore + rmssdScore + nn50Score + pnn50Score) / 6f
        return score
    }
}