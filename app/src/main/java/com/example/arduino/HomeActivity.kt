
package com.example.arduino

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.arduino.data.AppDatabase
import com.example.arduino.data.SessionMetricsEntity
import kotlin.math.pow
import kotlin.math.sqrt
import com.example.arduino.data.getStartAndEndOfSpecificDay
import com.example.arduino.data.groupRRToListOfLists
import com.example.arduino.data.formatSessionTime
import com.example.arduino.data.getTimeOfDayMessage
import kotlin.toString


class HomeActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arduinoManager.connect()
        arduinoManager.init(this)

        // Add data from intent
        handleIntent(intent)

        setContent {
            Dashboard {
                HomeActivityScreen(activityList = viewModel.activityList)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle new data if activity is already running
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent.getFloatArrayExtra("RR_DATA")?.toList()?.let { rrData ->
            val startingTime = intent.getStringExtra("STARTING_TIME") ?: ""
            val bpm = intent.getFloatExtra("BPM_DATA", 0f)
            val rrInterval = intent.getFloatExtra("RRINTERVAL_DATA", 0f)
            val dayTime = intent.getStringExtra("DAY_DATA") ?: ""
            viewModel.activityList.add(ActivityRecord(rrData, startingTime, bpm, rrInterval, dayTime))
            Log.d("HomeActivity", "RR Data added: $rrData at $startingTime")
            Log.d("HomeActivity", "Day Data added: $dayTime")
            viewModel.activityList.forEachIndexed { index, record ->
                Log.d("HomeViewModel", "Record #$index: $record")
            }

        }
    }
}


// ----------------------
// ArcProgress Composable
// ----------------------
@Composable
fun ArcProgressWithInnerLabels(
    modifier: Modifier = Modifier.fillMaxSize().padding(50.dp),
    arcDegrees: Float = 300f,
    targetProgress: Float,
    strokeWidth: Dp = 35.dp,
    backgroundColor: Color = Color.LightGray,
    foregroundColor: Color = Color.White,
    labels: List<Int> = listOf(0, 25, 50, 75, 100)
) {
    val progressAnim = remember { Animatable(0f) }

    val centerTextColor = colorResource(id = R.color.header_mine).toArgb()

    // Animate from 0 to targetProgress when composable is displayed
    LaunchedEffect(targetProgress) {
        progressAnim.animateTo(
            targetValue = targetProgress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 1500)
        )
    }

    val currentProgress = progressAnim.value

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2
        val centerX = size.width / 2
        val centerY = size.height / 2

        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        val startAngle = 270f - arcDegrees / 2f
        val sweepAngle = arcDegrees * currentProgress

        // Draw background arc
        drawArc(
            color = backgroundColor,
            startAngle = startAngle,
            sweepAngle = arcDegrees,
            useCenter = false,
            style = stroke
        )

        // Draw progress arc
        drawArc(
            color = foregroundColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = stroke
        )

        // Draw labels inside the stroke
        val labelRadius = radius - strokeWidth.toPx() * 0.009f
        val stepAngle = arcDegrees / (labels.size - 1)

        labels.forEachIndexed { index, label ->
            val angleRad = Math.toRadians((startAngle + stepAngle * index).toDouble())
            val x = centerX + labelRadius * kotlin.math.cos(angleRad)
            val y = centerY + labelRadius * kotlin.math.sin(angleRad)

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    label.toString(),
                    x.toFloat(),
                    y.toFloat() + 8f,
                    android.graphics.Paint().apply {
                        color = centerTextColor
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 34f
                        isAntiAlias = true
                        style = android.graphics.Paint.Style.FILL
                        typeface = android.graphics.Typeface.create(
                            android.graphics.Typeface.DEFAULT,
                            android.graphics.Typeface.BOLD
                        )
                    }
                )
            }
        }



        // Draw center text (two lines)
        val centerTextPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 50f
            isAntiAlias = true
            style = android.graphics.Paint.Style.FILL
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.BOLD
            )
        }

        // First line
        drawContext.canvas.nativeCanvas.drawText(
            "Health Score",
            centerX,
            centerY - 20f,
            centerTextPaint
        )

        // Second line
        drawContext.canvas.nativeCanvas.drawText(
            "${(currentProgress * 100).toInt()}%",
            centerX,
            centerY + 25f,
            centerTextPaint
        )
    }
}

// ----------------------
// Screen Composable
// ----------------------
@Composable
fun ArcProgressScreen(progress: Float) {
        ArcProgressWithInnerLabels(
            arcDegrees = 300f,
            targetProgress = progress, // Just set your progress here
            strokeWidth = 35.dp,
            modifier = Modifier.size(300.dp).padding(25.dp)
        )
}

// ----------------------
// MainActivity
// ----------------------

data class ActivityRecord(
    val rrData: List<Float>,   // RR intervals in ms
    val time: String,          // e.g., "Morning"
    val bpm: Float,            // Average BPM for the session
    val rrInterval: Float,     // Average RR interval in ms
    val dateTime: String       // Timestamp
) {
    /**
     * Compute RMSSD from rrData for HRV
     */
    val rmssd: Float
        get() {
            if (rrData.size < 2) return 0f
            val diffSquared = rrData.zipWithNext { a, b -> (b - a).pow(2) }
            return sqrt(diffSquared.average().toFloat())
        }
}

class HomeViewModel : ViewModel() {
    val activityList = mutableStateListOf<ActivityRecord>()
    val predictedList = mutableStateListOf<List<Float>>() // store predicted graphs
}

@Composable
fun SessionsForDayComposable() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)

    var rrBySession by remember {
        mutableStateOf<List<List<Float>>>(emptyList())
    }

    var sessions by remember { mutableStateOf<List<SessionMetricsEntity>>(emptyList()) }

    // Store activity records here
    var activityRecords by remember { mutableStateOf<List<ActivityRecord>>(emptyList()) }

    LaunchedEffect(Unit) {
        sessions = db.sessionMetricsDao().getSessionsByDayId(122025)
        val allRR = db.rrIntervalDao().getAllRRIntervalsOrdered()

        Log.d("MWA", "Sessions: ${sessions.size}")

        rrBySession = groupRRToListOfLists(allRR)

        // Create and store activity records
        activityRecords = sessions.mapIndexedNotNull { index, session ->
            val rrList = rrBySession.getOrNull(index) ?: emptyList()

            if (rrList.isNotEmpty()) {
                ActivityRecord(
                    rrData = rrList,
                    time = getTimeOfDayMessage(session.sessionId),
                    bpm = session.bpm,
                    rrInterval = rrList.average().toFloat(),
                    dateTime = formatSessionTime(session.sessionId)
                )
            } else {
                null
            }
        }

        // Log all records
        activityRecords.forEachIndexed { index, record ->
            Log.d("ActivityRecord", """
                Record $index:
                time: ${record.time}
                dateTime: ${record.dateTime}
                bpm: ${record.bpm}
                rrInterval: ${record.rrInterval}
                rmssd: ${record.rmssd}
            """.trimIndent())
        }
    }

    Column {
        // Display using stored records
        activityRecords.forEach { record ->
            ActivityLog(
                lineGraphData = record.rrData,
                testTime = record.dateTime,
                bpm = record.bpm,
                rrInterval = record.rrInterval,
                dayTime = record.time
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
@Composable
fun SessionsForDayToActivityList() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)

    var rrBySession by remember {
        mutableStateOf<List<List<Float>>>(emptyList())
    }

    var sessions by remember { mutableStateOf<List<SessionMetricsEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        sessions = db.sessionMetricsDao().getSessionsByDayId(122025)
        val allRR = db.rrIntervalDao().getAllRRIntervalsOrdered()

        Log.d("MWA", "Sessions: ${sessions.size}")

        rrBySession = groupRRToListOfLists(allRR)
    }

    Column {
        // Map each RR list to its corresponding session metadata
        sessions.forEachIndexed { index, session ->
            val rrList = rrBySession.getOrNull(index) ?: emptyList()

            // Only display if there are RR values
            if (rrList.isNotEmpty()) {
                ActivityRecord(
                    rrData = rrList,
                    time = formatSessionTime(session.sessionId),
                    bpm = session.bpm,
                    rrInterval = rrList.average().toFloat(),       // avg RR
                    dateTime = getTimeOfDayMessage(session.sessionId)
                )

                Log.d("time123", "${formatSessionTime(session.sessionId)}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun HomeActivityScreen(activityList: List<ActivityRecord>) {
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)

    SessionsForDayToActivityList()

    // Log all activity records
    Log.d("HomeActivity", "Total Activity Records: ${activityList.size}")

    activityList.forEachIndexed { index, record ->
        Log.d("HomeActivity", """
            =====================================
            Record ${index + 1}/${activityList.size}:
            - Time: ${record.time}
            - DateTime: ${record.dateTime}
            - BPM: ${record.bpm}
            - RR Interval: ${record.rrInterval}
            - RR Data Size: ${record.rrData.size}
            - RMSSD: ${record.rmssd}
            =====================================
        """.trimIndent())
    }

    val healthScore =  HealthScoreCalculator.calculate(
        activityList
    )

    Column(modifier = Modifier
        .verticalScroll(scrollState)
        .padding(top = 20.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("<", fontSize = 25.sp, fontWeight = FontWeight.Bold,
                color = Color.White)
            Spacer(modifier = Modifier.width(10.dp))

            Text("Dec 8, 2025", fontSize = 25.sp, fontWeight = FontWeight.Bold,
                color = Color.White)

            Spacer(modifier = Modifier.width(10.dp))

            Text(">", fontSize = 25.sp, fontWeight = FontWeight.Bold,
                color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))


        ArcProgressScreen(progress = healthScore / 100f)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "ACTIVITIES",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Start),
        )

        Spacer(modifier = Modifier.height(10.dp))

        SessionsForDayComposable()

        Spacer(modifier = Modifier.height(10.dp))

        activityList.forEach { record ->
            ActivityLog(
                lineGraphData = record.rrData,
                testTime = record.time,
                bpm = record.bpm,
                rrInterval = record.rrInterval,
                dayTime = record.dateTime
            )
            Spacer(modifier = Modifier.height(7.dp))
        }
    }
}

@Composable
fun ActivityLog(lineGraphData: List<Float>,
    testTime: String,
    bpm: Float,
    rrInterval: Float,
    dayTime: String
) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White)
        .height(148.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)) {
            Box(modifier = Modifier
                .weight(0.35f)
                .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier
                    .wrapContentWidth(),  // Change from fillMaxWidth() to wrapContentWidth()
                    verticalArrangement = Arrangement.Center// Add this
                ) {

                    Text(text = dayTime, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    Text(text = testTime, fontWeight = FontWeight.Bold)

                    Image(
                        painter = painterResource(id = R.drawable.vector),
                        contentDescription = "Arrow",
                        modifier = Modifier
                            .size(width = 50.41.dp, height = 28.35.dp)
                    )

                    var description by remember { mutableStateOf(
                        "Recent heart metrics show an average " +
                                "heart rate of 72 bpm"
                    )}

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "HEART METRICS", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxHeight()
                            .weight(0.5f)
                        ) {
                            Column(modifier = Modifier
                                .fillMaxWidth()) {
                                Text("BPM", fontSize = 9.sp)

                                val bpmText = String.format("%.1f", bpm)

                                Text(text = bpmText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        Box(modifier = Modifier.fillMaxHeight()
                            .weight(0.5f)
                        ) {
                            Column(modifier = Modifier
                                .fillMaxWidth()) {

                                val rrIntervalText = String.format("%.1f", rrInterval)
                                Text("RR INTERVAL", fontSize = 9.sp)
                                Text(text = rrIntervalText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }



                }
            }

            Spacer(modifier = Modifier.width(5.dp))

            val heartRatePredicted = lineGraphData.map { value ->
                val randomOffset = (-20..20).random()   // random integer between -5 and 5
                (value + randomOffset).coerceAtLeast(0f)  // ensure no negative values
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(0.65f)) {
                LineGraph(data = lineGraphData, predicted = null)
            }
        }
    }
}