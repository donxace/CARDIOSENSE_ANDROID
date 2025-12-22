package com.example.arduino.model

data class HeartMetrics(
    val bpm: Float,
    val rrInterval: Float,
    val sdnn: Float,
    val rmssd: Float,
    val nn50: Int,
    val pnn50: Float,
    val timestamp: String
)