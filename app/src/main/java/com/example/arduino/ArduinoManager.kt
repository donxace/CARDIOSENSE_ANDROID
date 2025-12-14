package com.example.arduino


import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.arduino.data.RRInterval
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.platform.android.AndroidLogHandler.close
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

import android.content.Context
import com.example.arduino.data.AppDatabase
import com.example.arduino.data.RRIntervalDao

class ArduinoManager(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val rrDao: RRIntervalDao = db.rrIntervalDao()

    var statusMessage by mutableStateOf("Disconnected")
    val _dataPoints = mutableStateListOf<Float>()
    val dataPoints: List<Float> get() = _dataPoints
    private val currentSessionData = mutableListOf<RRInterval>()
    private var currentSessionId: Long = 0L

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    private var receiveJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val TAG = "ArduinoWiFi"
    private val ip = "192.168.1.179"
    private val port = 80

    fun startSession() {
        currentSessionId = System.currentTimeMillis() // unique session
        currentSessionData.clear()                     // clear previous session data
        Log.d("ArduinoManager", "✅ startSession() called | sessionId=$currentSessionId")
    }

    fun addRRInterval(rrValue: Float) {
        _dataPoints.add(rrValue)

        if (_dataPoints.size > 200) _dataPoints.removeAt(0) // keep graph limited

        val rrInterval = RRInterval(
            sessionId = currentSessionId,
            timestamp = System.currentTimeMillis(),
            rrValue = rrValue
        )
        currentSessionData.add(rrInterval)

        Log.d(
            "ArduinoManager",
            "➕ addRRInterval() called | rrValue=$rrValue | currentSessionDataSize=${currentSessionData.size}"
        )
    }

    fun endSession() {
        Log.d(
            "ArduinoManager",
            "🛑 endSession() called | saving ${currentSessionData.size} RR intervals to database"
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                rrDao.insertAll(currentSessionData)
                Log.d("ArduinoManager", "💾 endSession() | insertAll() success")

                // Now read back the inserted data
                val allData = rrDao.getAllBySession(currentSessionId)
                allData.forEach {
                    Log.d("RRIntervalDB", "id=${it.id}, sessionId=${it.sessionId}, rr=${it.rrValue}")
                }
            } catch (e: Exception) {
                Log.e("ArduinoManager", "❌ endSession() | insertAll() failed: ${e.message}")
            }
        }

        currentSessionData.clear()
        Log.d("ArduinoManager", "currentSessionData cleared")
    }


    fun connect() {
        scope.launch {
            while (isActive) {
                try {
                    socket = Socket(ip, port)
                    writer = PrintWriter(socket!!.getOutputStream(), true)
                    reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                    withContext(Dispatchers.Main) { statusMessage = "Connected" }
                    startReceiving()
                    break
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { statusMessage = "Connection failed" }
                    delay(3000)
                }
            }
        }
    }

    private fun startReceiving() {
        if (receiveJob != null) return
        receiveJob = scope.launch {
            while (isActive && socket?.isConnected == true) {
                val line = reader?.readLine()
                if (line == null) {
                    withContext(Dispatchers.Main) { statusMessage = "Arduino disconnected" }
                    Log.d(TAG, "Disconnected, reconnecting...")
                    reconnect()
                    break  // Allowed here because it's in a normal while loop
                }
                updateData(line)
            }
        }
    }

    private fun updateData(line: String) {
        Log.d(TAG, "updateData() called with: '$line'")

        val rr = line.trim().toFloatOrNull()

        if (rr == null) {
            Log.e(TAG, "❌ Failed to parse Float from: '$line'")
            return
        }

        addRRInterval(rr)

        scope.launch(Dispatchers.Main) {
            _dataPoints.add(rr)

            if (_dataPoints.size > 200) {
                _dataPoints.removeAt(0)
            }

            Log.d(
                TAG,
                "✅ _dataPoints updated | size=${_dataPoints.size}, last=${_dataPoints.last()}"
            )
        }
    }
    suspend fun reconnect() {
        close()
        delay(2000)
        connect()
    }

    fun sendCommand(cmd: String) {
        scope.launch {
            try {
                writer?.println(cmd)
                writer?.flush()
                withContext(Dispatchers.Main) { statusMessage = "Sent $cmd" }
                Log.d(TAG, "Sent: $cmd")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { statusMessage = "Send error: ${e.message}" }
                Log.e(TAG, "Send error: ${e.message}")
                reconnect()
            }
        }
    }

    fun close() {
        try { writer?.close() } catch (_: Exception) {}
        try { reader?.close() } catch (_: Exception) {}
        try { socket?.close() } catch (_: Exception) {}
        socket = null
        writer = null
        reader = null
        receiveJob?.cancel()
        receiveJob = null
    }
}