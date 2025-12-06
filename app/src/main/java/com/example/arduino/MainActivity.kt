package com.example.arduino

import androidx.compose.material3.MaterialTheme
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import android.content.Intent
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen



class MainActivity : ComponentActivity() {

    private var isLoggedIn by mutableStateOf(false)

    private val TAG = "ArduinoWiFi"
    private val arduinoIP = "192.168.1.179"
    private val arduinoPort = 80

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null

    private var receiveJob: Job? = null

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Graph data
    private val _dataPoints = mutableStateListOf<Float>()
    val dataPoints: List<Float> get() = _dataPoints

    // UI text for Arduino command responses
    var statusMessage by mutableStateOf("Press a button to control Arduino")

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                if (!isLoggedIn) {
                    // Show Login UI

                    // Show Arduino Control UI
                    ArduinoControlApp(
                        statusMessage = { statusMessage },
                        dataPoints = { dataPoints },
                        onSendCommand = { cmd -> sendCommand(cmd) }
                    )
                }
            }
        }

        connectToArduino()
    }

    // --------------------------------------------------------------
    // CONNECT TO ARDUINO
    // --------------------------------------------------------------
    private fun connectToArduino() {
        scope.launch {
            while (isActive) {
                try {
                    socket = Socket(arduinoIP, arduinoPort)
                    writer = PrintWriter(socket!!.getOutputStream(), true)
                    reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

                    withContext(Dispatchers.Main) {
                        statusMessage = "Connected to Arduino"
                    }
                    Log.d(TAG, "Socket connected")

                    startReceiving()
                    break
                } catch (e: Exception) {
                    Log.e(TAG, "Connection failed: ${e.message}")
                    withContext(Dispatchers.Main) {
                        statusMessage = "Connection failed, retrying..."
                    }
                    delay(3000)
                }
            }
        }
    }

    // --------------------------------------------------------------
    // CONTINUOUS RECEIVE LOOP
    // --------------------------------------------------------------
    private fun startReceiving() {
        if (receiveJob != null) return

        receiveJob = scope.launch {
            try {
                while (isActive && socket?.isConnected == true) {
                    val line = reader?.readLine()
                    if (line == null) {
                        withContext(Dispatchers.Main) { statusMessage = "Arduino disconnected" }
                        Log.d(TAG, "Disconnected, reconnecting...")
                        reconnect()
                        break
                    }

                    Log.d(TAG, "Received: $line")
                    updateData(line)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { statusMessage = "Receive error: ${e.message}" }
                Log.e(TAG, "Receive error: ${e.message}")
                reconnect()
            }
        }
    }

    // --------------------------------------------------------------
    // RECONNECT
    // --------------------------------------------------------------
    private suspend fun reconnect() {
        closeSocket()
        delay(2000)
        connectToArduino()
    }

    // --------------------------------------------------------------
    // SEND COMMAND TO ARDUINO
    // --------------------------------------------------------------
    private fun sendCommand(cmd: String) {
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

    // --------------------------------------------------------------
    // UPDATE GRAPH DATA
    // --------------------------------------------------------------
    private fun updateData(line: String) {
        val rr = line.toFloatOrNull() ?: return
        scope.launch(Dispatchers.Main) {
            _dataPoints.add(rr)
            if (_dataPoints.size > 200) _dataPoints.removeAt(0)
        }
    }

    // --------------------------------------------------------------
    // CLOSE SOCKET
    // --------------------------------------------------------------
    private fun closeSocket() {
        try { writer?.close() } catch (_: Exception) {}
        try { reader?.close() } catch (_: Exception) {}
        try { socket?.close() } catch (_: Exception) {}
        writer = null
        reader = null
        socket = null
        receiveJob?.cancel()
        receiveJob = null
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        closeSocket()
    }
}

@Composable
fun ArduinoControlApp(
    statusMessage: () -> String,
    dataPoints: () -> List<Float>,
    onSendCommand: (String) -> Unit
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Status message
            Text(text = statusMessage(), style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Row {
                Button(onClick = { onSendCommand("ON") }) {
                    Text("Start Monitor")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { onSendCommand("OFF") }) {
                    Text("Stop Monitor")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live RR intervals list (optional)
            RealTimeLineGraph(dataPoints())

            DataList(dataPoints())
        }
    }
}

@Composable
fun RealTimeLineGraph(data: List<Float>) {
    if (data.isEmpty()) return

    val maxVal = (data.maxOrNull() ?: 100f) + 10f
    val minVal = (data.minOrNull() ?: 0f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(width = 2.dp, color = Color.Black)
            .padding(start = 40.dp, bottom = 20.dp) // Space for axis labels
    ) {
        val widthStep = size.width / (data.size - 1).coerceAtLeast(1)
        val heightRange = maxVal - minVal

        // Draw Y-axis lines & labels
        val ySteps = 5
        val yStepValue = heightRange / ySteps
        for (i in 0..ySteps) {
            val y = size.height - (i / ySteps.toFloat() * size.height)
            drawLine(
                color = Color.LightGray,
                start = androidx.compose.ui.geometry.Offset(0f, y),
                end = androidx.compose.ui.geometry.Offset(size.width, y),
                strokeWidth = 1f
            )
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    String.format("%.0f", minVal + i * yStepValue),
                    -35f,
                    y + 5f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 30f
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
            }
        }

        // Draw the graph line
        val path = Path()
        data.forEachIndexed { index, value ->
            val x = index * widthStep
            val y = size.height - ((value - minVal) / heightRange * size.height)

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(path = path, color = Color.Red, style = Stroke(width = 3f))

        data.forEachIndexed { index, value ->
            val x = index * widthStep
            if(index % 10 == 0) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${index + 1}",
                        x,
                        size.height + 20f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 30f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun DataList(data: List<Float>) {
    Column(modifier = Modifier.padding(16.dp)) {
        data.forEachIndexed { index, value ->
            Text(text = "RR #${index + 1}: $value ms")
        }
    }
}
