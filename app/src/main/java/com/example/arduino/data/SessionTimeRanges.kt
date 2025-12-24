package com.example.arduino.data

data class SessionTimeRanges(
    val last24Hours: List<SessionMetricsEntity>,
    val lastWeek: List<SessionMetricsEntity>,
    val lastYear: List<SessionMetricsEntity>
)
