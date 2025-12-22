package com.example.arduino


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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


import android.content.Context
import androidx.compose.runtime.remember
import com.example.arduino.data.AppDatabase
import com.example.arduino.data.RRIntervalDao
import com.example.arduino.data.SessionMetricsEntity
import com.example.arduino.data.generateDaySessionId
import java.io.ByteArrayOutputStream
import java.net.InetSocketAddress
import java.net.SocketTimeoutException
import java.util.Calendar

// Last session metrics
var lastAverageRR: Float = 0f
var lastBPM: Float = 0f
var lastSDNN: Float = 0f
var lastRMSSD: Float = 0f
var lastNN50: Int = 0
var lastPNN50: Float = 0f

object arduinoManager {

    // State for connection
    var isConnected by mutableStateOf(false)
        private set

    // ---------- CONFIG ----------
    private lateinit var appContext: Context
    private val TAG = "ArduinoWiFi"
    private val ip = "192.168.1.179"
    private val port = 80

    // ---------- COROUTINE & SOCKET ----------
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    private var receiveJob: Job? = null

    // ---------- DATABASE ----------
    lateinit var rrDao: RRIntervalDao

    // ---------- DATA ----------
    var statusMessage by mutableStateOf("Disconnected")
    private val _dataPoints = mutableStateListOf<Float>()
    val dataPoints: List<Float> get() = _dataPoints
    private val currentSessionData = mutableListOf<RRInterval>()
    var currentSessionId: Long = 0L
        private set

    var currentSessionStartTime: Long = 0
        private set

    var startTime = mutableStateOf("")
        private set

    var day = mutableStateOf("")

    var time = startTime





    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())  // 12-hour format


    fun resetCurrentDatas() {
        time.value = ""
        _dataPoints.clear()
        Log.d(TAG, "📉 resetGraphPoints() → graph cleared")
    }

    // ---------- INITIALIZATION ----------
    fun init(context: Context) {
        appContext = context.applicationContext
        val db = AppDatabase.getDatabase(context.applicationContext)
        rrDao = db.rrIntervalDao()
    }

    // ---------- SESSION MANAGEMENT ----------
    fun startSession() {
        currentSessionId = System.currentTimeMillis()
        currentSessionStartTime = currentSessionId
        currentSessionData.clear()
        Log.d(TAG, "✅ startSession() | sessionId=$currentSessionId")
    }

    fun addRRInterval(rrValue: Float) {
        if (_dataPoints.size > 200) _dataPoints.removeAt(0)

        val rrInterval = RRInterval(
            sessionId = currentSessionId,
            timestamp = generateDaySessionId(),
            rrValue = rrValue,
            sessionStartTime = currentSessionStartTime
        )
        currentSessionData.add(rrInterval)

        scope.launch(Dispatchers.Main) {
            _dataPoints.add(rrValue)
            if (_dataPoints.size > 200) _dataPoints.removeAt(0)
        }

        Log.d(TAG, "➕ addRRInterval() | rrValue=$rrValue | size=${currentSessionData.size}")
    }

    fun getTimeOfDayFromString(timeString: String?): String {
        if (timeString.isNullOrEmpty()) return "Unknown"  // <-- avoid parse error

        return try {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = sdf.parse(timeString) ?: return "Unknown"

            val calendar = Calendar.getInstance()
            calendar.time = date
            val hour = calendar.get(Calendar.HOUR_OF_DAY)

            when (hour) {
                in 5..11 -> "MORNING TEST"
                in 12..16 -> "AFTERNOON TEST"
                in 17..20 -> "EVENING TEST"
                else -> "EVENING TEST"
            }
        } catch (e: Exception) {
            "Unknown"  // fallback if parsing fails
        }
    }




    fun endSession() {
        val sessionIdToSave = currentSessionId
        val dataToSave = currentSessionData.toList() // snapshot

        day.value = getTimeOfDayFromString(startTime.value)

        startTime.value = sdf.format(Date(sessionIdToSave))

        Log.d("starttime", "${startTime.value}")

        // Log all RR intervals
        val rrValuesString = dataToSave.joinToString(separator = ", ") { it.rrValue.toString() }
        Log.d(TAG, "RR intervals for session $currentSessionId: [$rrValuesString]")

        val rrValues = dataToSave.map { it.rrValue }

        val averageRR = if (rrValues.isNotEmpty()) rrValues.average().toFloat() else 0f
        val bpm = if (averageRR > 0) 60000f / averageRR else 0f

        val sdnn = if (rrValues.isNotEmpty()) {
            val mean = rrValues.average()
            kotlin.math.sqrt(rrValues.map { (it - mean).let { d -> d * d } }.average()).toFloat()
        } else 0f

        val rmssd = if (rrValues.size >= 2) {
            val diffs = rrValues.zipWithNext { a, b -> b - a }
            kotlin.math.sqrt(diffs.map { it * it }.average()).toFloat()
        } else 0f

        val nn50 = if (rrValues.size >= 2) {
            val diffs = rrValues.zipWithNext { a, b -> kotlin.math.abs(b - a) }
            diffs.count { it > 50f }
        } else 0

        val pnn50 = if (rrValues.size >= 2) {
            nn50.toFloat() / (rrValues.size - 1) * 100f
        } else 0f

        // Store in ArduinoManager properties
        lastAverageRR = averageRR
        lastBPM = bpm
        lastSDNN = sdnn
        lastRMSSD = rmssd
        lastNN50 = nn50
        lastPNN50 = pnn50

        Log.d(TAG, "Session Metrics → AvgRR=$averageRR ms | BPM=$bpm | SDNN=$sdnn | RMSSD=$rmssd | NN50=$nn50 | pNN50=$pnn50")

        // Save to Room
        scope.launch(Dispatchers.IO) {
            try {
                rrDao.insertAll(dataToSave)
                rrDao.insertMetrics(
                    SessionMetricsEntity(
                        sessionId = sessionIdToSave,
                        avgRR = averageRR,
                        bpm = bpm,
                        sdnn = sdnn,
                        rmssd = rmssd,
                        nn50 = nn50,
                        pnn50 = pnn50,
                        sessionStartTime = generateDaySessionId()
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save session: ${e.message}", e)
            }
        }

        // Clear session
        currentSessionData.clear()
    }

    // ---------- SOCKET CONNECTION ----------
    fun connect() {
        // Prevent multiple connections
        if (socket?.isConnected == true) {
            Log.d(TAG, "Already connected, skipping connect()")
            return
        }

        val timeoutMillis = 5000 // 5 seconds

        scope.launch {
            while (isActive) {
                try {
                    Log.d(TAG, "Attempting to connect to $ip:$port")

                    socket = Socket()
                    socket!!.connect(InetSocketAddress(ip, port), timeoutMillis)
                    socket!!.soTimeout = 5000 // read timeout

                    writer = PrintWriter(socket!!.getOutputStream(), true)
                    reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

                    Log.i(TAG, "Connected to $ip:$port")

                    withContext(Dispatchers.Main) {
                        isConnected = true
                        statusMessage = "Connected"
                    }

                    startReceiving()
                    break

                } catch (e: SocketTimeoutException) {
                    Log.e(TAG, "Connection timed out")
                    withContext(Dispatchers.Main) {
                        isConnected = false
                        statusMessage = "Connection timed out"
                    }
                    delay(3000) // Retry

                } catch (e: Exception) {
                    Log.e(TAG, "Connection failed: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        isConnected = false
                        statusMessage = "Connection failed"
                    }
                    delay(3000) // Retry
                }
            }
        }
    }

    private fun startReceiving() {
        scope.launch(Dispatchers.IO) {
            val input = socket?.getInputStream() ?: return@launch
            val buffer = ByteArray(1024)
            val baos = ByteArrayOutputStream()

            try {
                while (isActive && socket?.isConnected == true) {
                    try {
                        val bytesRead = input.read(buffer)
                        if (bytesRead == -1) throw Exception("Stream closed")

                        // Append to ByteArrayOutputStream
                        baos.write(buffer, 0, bytesRead)

                        // Convert to string and process complete messages
                        val data = baos.toString("UTF-8")
                        if (data.contains("\n")) {
                            val messages = data.split("\n")
                            for (msg in messages.dropLast(1)) { // all complete messages
                                Log.d(TAG, "Received: $msg")
                                updateData(msg)
                            }
                            // Keep leftover partial message in baos
                            baos.reset()
                            baos.write(messages.last().toByteArray(Charsets.UTF_8))
                        }

                        // Connection is alive, mark as connected
                        withContext(Dispatchers.Main) {
                            isConnected = true
                            statusMessage = "Connected"
                        }

                    } catch (e: SocketTimeoutException) {
                        Log.w(TAG, "Read timed out, attempting to reconnect...")
                        handleDisconnect()
                        break
                    } catch (e: Exception) {
                        Log.e(TAG, "Connection lost: ${e.message}")
                        handleDisconnect()
                        break
                    }


                }
            } catch (e: Exception) {
                Log.e(TAG, "Receiving error: ${e.message}")
                handleDisconnect()
            } finally {
                // Ensure disconnected state
                withContext(Dispatchers.Main) {
                    isConnected = false
                    statusMessage = "Disconnected"
                }
                close()
            }
        }
    }

    private fun handleDisconnect() {
        scope.launch(Dispatchers.Main) {
            isConnected = false
            statusMessage = "Disconnected"
        }
        close() // Close socket safely

        // Retry after delay
        scope.launch {
            delay(3000) // 3 seconds
            Log.d(TAG, "Re-attempting connection...")
            connect()
        }
    }


    private fun updateData(line: String) {
        Log.d(TAG, "updateData() called with: '$line'")

        val rr = line.trim().toFloatOrNull()

        //
        if (rr == null) {
            Log.e(TAG, "❌ Failed to parse Float from: '$line'")
            return
        }
        addRRInterval(rr)

    }

    // ---------- RECONNECT ----------
    suspend fun reconnect() {
        close()
        delay(2000)
        connect()
    }

    // ---------- SEND COMMAND ----------
    fun sendCommand(cmd: String) {
        scope.launch {
            try {
                writer?.println(cmd)
                writer?.flush()
                withContext(Dispatchers.Main) { statusMessage = "Sent $cmd" }
                Log.d(TAG, "Sent: $cmd")
            } catch (e: Exception) {
                Log.e(TAG, "Send error: ${e.message}", e)
                withContext(Dispatchers.Main) { statusMessage = "Send error: ${e.message}" }
                reconnect()
            }
        }
    }

    // ---------- CLOSE SOCKET ----------
    fun close() {
        try {
            Log.d("DB_DEBUG", "Closing database!")

            reader?.close()
            writer?.close()
            socket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing socket: ${e.message}")
        } finally {
            reader = null
            writer = null
            socket = null
        }
    }

    fun resetSessionMetrics() {
        lastAverageRR = 0f
        lastBPM = 0f
        lastSDNN = 0f
        lastRMSSD = 0f
        lastNN50 = 0
        lastPNN50 = 0f
    }
}