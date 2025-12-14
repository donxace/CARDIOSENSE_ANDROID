package com.example.arduino

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin


class TimerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val arduinoManager = ArduinoManager(context = this)



        setContent {
            Dashboard {
                var isRunning by remember { mutableStateOf(false) }
                var totalTime = 10 // Or your total time
                var timeLeft by remember { mutableStateOf(totalTime) }
                var progress by remember { mutableStateOf(0f) }

                val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

                LaunchedEffect(isRunning) {
                    while (isRunning && timeLeft > 0) {
                        delay(1000)
                        timeLeft -= 1
                        progress = 1f - (timeLeft / totalTime.toFloat())
                    }
                    if (timeLeft == 0) isRunning = false
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .background(Color.White),
                ) {
                    Column(
                        modifier = Modifier
                            .background(colorResource(R.color.orange_mine))
                            .fillMaxWidth()
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {


                        // UI Circle
                        CircularTimer(progress = progress, timeLeft = timeLeft)

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(top= 20.dp)


                        ) {
                            RealTimeLineGraph(arduinoManager.dataPoints)
                        }

                        Spacer(modifier = Modifier.height(10.dp))


                        Box(
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .padding(horizontal = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = { arduinoManager.sendCommand("ON")
                                              isRunning = true
                                              arduinoManager.startSession() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colorResource(R.color.header_mine),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Start")
                                }

                                Spacer(modifier = Modifier.width(5.dp))

                                Button(
                                    onClick = { arduinoManager.sendCommand("OFF")
                                        arduinoManager.endSession()
                                        isRunning = false},
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colorResource(R.color.header_mine),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Stop")
                                }

                                Spacer(modifier = Modifier.width(5.dp))

                                Button(
                                    onClick = { scope.launch { arduinoManager.reconnect() } },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colorResource(R.color.header_mine),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Reconnect")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CircularTimer(
    progress: Float,
    timeLeft: Int,
    modifier: Modifier = Modifier.size(250.dp),
    strokeWidth: Dp = 10.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 1000,  // Increased duration for smoother animation
            easing = FastOutSlowInEasing  // Smooth easing curve
        ),
        label = "progress"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val center = this.center

            val arcSize = size.minDimension - strokePx
            val radius = arcSize / 2f  // Center of the stroke

            // Background arc (gray)
            drawArc(
                color = Color(0xFFE0E0E0),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(strokePx / 2f, strokePx / 2f),
                size = Size(arcSize, arcSize)
            )

            // Progress arc (blue)
            drawArc(
                color = Color.White,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(strokePx / 2f, strokePx / 2f),
                size = Size(arcSize, arcSize)
            )

            // Circle at the end of progress
            val progressAngle = 360 * animatedProgress
            val angleRad = Math.toRadians((progressAngle - 90).toDouble())

            val dotX = center.x + cos(angleRad) * radius
            val dotY = center.y + sin(angleRad) * radius

            drawCircle(
                color = Color.White,
                radius = strokePx,
                center = Offset(dotX.toFloat(), dotY.toFloat())
            )
        }
        // Center timer text in 0:00 format
        val minutes = timeLeft / 60
        val seconds = timeLeft % 60
        Text(
            text = String.format("%d:%02d", minutes, seconds),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

