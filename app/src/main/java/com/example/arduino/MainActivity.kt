package com.example.arduino

import androidx.compose.ui.graphics.Brush
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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

            }
        }

        connectToArduino()
    }

    // --------------------------------------------------------------
    // CONNECT TO ARDUINO
    // --------------------------------------------------------------

    fun connectToArduino() {
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
    suspend fun reconnect() {
        closeSocket()
        delay(2000)
        connectToArduino()
    }

    // --------------------------------------------------------------
    // SEND COMMAND TO ARDUINO
    // --------------------------------------------------------------
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

    // --------------------------------------------------------------
    // UPDATE GRAPH DATA
    // --------------------------------------------------------------
    private fun updateData(line: String) {
        val rr = line.toFloatOrNull() ?: return
        scope.launch(Dispatchers.Main) {
            _dataPoints.add(rr)
            Log.d("GraphDebug", "Added $rr | size=${_dataPoints.size}")
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
    onSendCommand: (String) -> Unit,
    reconnect: suspend () -> Unit
) {
    val scope = rememberCoroutineScope()

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
                onButton (
                    onClick = { onSendCommand("ON") }
                )

                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { onSendCommand("OFF") }) {
                    Text("Stop Monitor")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    scope.launch { reconnect() } // launch suspend function
                }) {
                    Text("Reconnect")
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            RealTimeLineGraph(dataPoints())

            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(0.65f)) {
                LineGraph(data = heartRateData, predicted = null, modifier = Modifier.padding(start = 40.dp, top = 15.dp, bottom = 20.dp, end = 20.dp))
            }

            DataList(dataPoints())
        }
    }
}

@Composable
fun onButton(
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text("Start")
    }
}

@Composable
fun RealTimeLineGraph(data: List<Float>) {
    // If there is no data, do nothing
    if (data.isEmpty()) return

    // Determine the maximum and minimum values of the data for scaling the Y-axis
    val maxVal = 900f
    val minVal = 500f

    // Canvas for drawing the graph
    Canvas(
        modifier = Modifier
            .fillMaxWidth() // Take full width
            .height(140.dp) // Set height
            .background(Color.White) // Background color
            .padding(start = 50.dp, top = 30.dp, bottom = 30.dp, end = 20.dp) // Padding for axis labels
    ) {
        // Horizontal spacing between data points
        val widthStep = size.width / (data.size - 1).coerceAtLeast(1)
        // Total range of Y values
        val heightRange = maxVal - minVal

        // Draw Y-axis lines & labels
        val numberOfLabels = 5
        val yIncrement = 100f

// Round min and max to nearest multiple of 100
        val roundedMin = (minVal / yIncrement).toInt() * yIncrement
        val roundedMax = ((maxVal / yIncrement).toInt() + 1) * yIncrement

// Calculate the spacing between the 5 labels
        val stepValue = (roundedMax - roundedMin) / (numberOfLabels - 1)

        for (i in 0 until numberOfLabels) {
            val labelValue = roundedMin + i * stepValue
            val y = size.height - ((labelValue - minVal) / (maxVal - minVal) * size.height)

            // Draw horizontal grid line
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )

            // Draw Y-axis label
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    String.format("%.0f", labelValue),
                    -26f,
                    y + 5f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 30f
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
            }
        }


        // --- Prepare the full path of the graph line ---
        data.forEachIndexed { index, value ->
            val x = index * widthStep
            val y = size.height - ((value - minVal) / heightRange * size.height)

            val gradient = Brush.verticalGradient(
                colors = listOf(Color.Red, Color.Transparent), // top to bottom
                startY = y,     // the top of the line
                endY = size.height // bottom of the line
            )

            drawLine(
                brush = gradient,   // use the gradient instead of a solid color
                start = Offset(x, y),     // top
                end = Offset(x, size.height), // bottom
                strokeWidth = 4f
            )
        }

        // --- Prepare animated path (currently just same as path) ---
        val animatedPath = Path()
        data.forEachIndexed { index, value ->
            val x = index * widthStep
            val y = size.height - ((value - minVal) / heightRange * size.height)

            if (index == 0) animatedPath.moveTo(x, y)
            else animatedPath.lineTo(x, y)
        }

        // Draw the graph line
        drawPath(
            path = animatedPath,
            color = Color.Red,
            style = Stroke(width = 5f) // Thickness of line
        )

        // --- Draw X-axis labels every 10 points ---
        data.forEachIndexed { index, value ->
            val x = index * widthStep
            if (index % 10 == 0) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${index}", // Label
                        x,
                        size.height + 20f, // Position below the graph
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
