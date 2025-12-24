package com.example.arduino.domain

/**
 * Represents the calculated averages for session metrics over a time range.
 *
 * @property avgBpm Average beats per minute
 * @property avgRR Average RR interval
 */
data class SessionAverages(
    val avgBPM: Float,
    val avgSDNN: Float,
    val avgRMSSD: Float,
    val avgNN50: Int,
    val avgPNN50: Float,
    val avgRR: Float
)
