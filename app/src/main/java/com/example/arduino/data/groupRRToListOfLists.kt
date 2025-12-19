package com.example.arduino.data

import com.example.arduino.data.RRInterval

fun groupRRToListOfLists(
    rrIntervals: List<RRInterval>
): List<List<Float>> {
    return rrIntervals
        .groupBy { it.sessionId }
        .toSortedMap()
        .values
        .map { sessionList ->
            sessionList.map { it.rrValue }
        }
}
