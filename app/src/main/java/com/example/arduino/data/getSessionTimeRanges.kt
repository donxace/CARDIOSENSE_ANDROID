package com.example.arduino.data

suspend fun getSessionTimeRanges(sessionDao: SessionMetricsDao): SessionTimeRanges {
    val last24Hours = sessionDao.getLastNHours(24)
    val lastWeek = sessionDao.getLastNDays(7)
    val lastYear = sessionDao.getLastNYears(1)

    return SessionTimeRanges(
        last24Hours = last24Hours,
        lastWeek = lastWeek,
        lastYear = lastYear
    )
}
