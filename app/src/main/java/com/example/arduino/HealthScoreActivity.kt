package com.example.arduino

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arduino.DashboardActivity.PieChartData
import com.example.arduino.data.AppDatabase
import com.example.arduino.util.calculateDailyHealthScore
import com.example.arduino.util.calculateWeeklyHealthScore
import com.example.arduino.util.startOfDay
import com.example.arduino.util.weekStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

val heartRateData = listOf(10f, 40f, 67f, 78f)
val heartRatePredicted = listOf(818f, 820f, 810f, 825f)

val pieData = listOf(
    PieChartData(20f, Color(0XFF6E4648), "Monday"),
    PieChartData(10f, Color(0XFFFF9C9C), "Tuesday"),
    PieChartData(30f, Color(0XFFF6391E), "Wednesday"),
    PieChartData(25f, Color(0XFF4B0900), "Thursday"),
    PieChartData(15f, Color(0XFF000000), "Friday")
)

class HealthScoreActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val db = AppDatabase.getDatabase(this) // 'this' is a valid context
        val sessionDao = db.sessionMetricsDao()
        val dailyDao = db.dailyHealthScoreDao()
        val weeklyDao = db.weeklyHealthScoreDao()

        super.onCreate(savedInstanceState)
        setContent {
            Dashboard {
                HealthScoreScreen()
            }
        }
        val now = System.currentTimeMillis()

        CoroutineScope(Dispatchers.Main).launch {
            calculateDailyHealthScore(sessionDao, dailyDao)
            calculateWeeklyHealthScore(dailyDao, weeklyDao, weekStart(now))
        }

    }


}

@Composable
fun HealthScoreScreen() {
    val context = LocalContext.current
    val activity = context as Activity
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()  // Compose-aware coroutine scope

    var date by remember { mutableStateOf("NOVEMBER")}

        Column( modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.White)
                            .height(144.99.dp)

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left half: PieChart centered
                            Box(
                                modifier = Modifier
                                    .weight(1f)           // takes half of the Row
                                    .height(126.78.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                PieChart(
                                    data = pieData,
                                    chartSize = 64.75.dp,
                                    innerCircleSize = 39.dp
                                )
                            }

                            // Right half: Text centered vertically
                            Box(
                                modifier = Modifier
                                    .weight(1f),
                                contentAlignment = Alignment.Center  // align content to top-left
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Top,  // stack texts from top
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text("November", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Weekly Report", color = Color.Black, fontSize = 12.sp)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f),
                                            contentAlignment = Alignment.TopStart
                                        ) {
                                            Row() {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp) // circle diameter same as font size
                                                        .background(color = Color(0xFFFF9C9C), shape = CircleShape)
                                                )
                                                Text("   Monday", fontSize = 8.sp, color = Color.Black)
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f),
                                            contentAlignment = Alignment.TopStart
                                        ) {
                                            Row() {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp) // circle diameter same as font size
                                                        .background(color = Color(0xFFF6391E), shape = CircleShape)
                                                )
                                                Text("   Tuesday", fontSize = 8.sp, color = Color.Black)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Row(
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f),
                                            contentAlignment = Alignment.TopStart
                                        ) {
                                            Row() {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp) // circle diameter same as font size
                                                        .background(color = Color(0xFF4B0900), shape = CircleShape)
                                                )
                                                Text("   Wednesday", fontSize = 8.sp, color = Color.Black)
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f),
                                            contentAlignment = Alignment.TopStart
                                        ) {
                                            Row() {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp) // circle diameter same as font size
                                                        .background(color = Color.Black, shape = CircleShape)
                                                )
                                                Text("   Thursday", fontSize = 8.sp, color = Color.Black)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Row(
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f),
                                            contentAlignment = Alignment.TopStart
                                        ) {
                                            Row() {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp) // circle diameter same as font size
                                                        .background(color = Color(0xFF6E4648), shape = CircleShape)
                                                )
                                                Text("   Friday", fontSize = 8.sp, color = Color.Black)
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f),
                                            contentAlignment = Alignment.TopStart
                                        ) {
                                            Text("", fontSize = 8.sp, color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier

                        .height(202.7.dp)) {


                        Box(modifier = Modifier
                            .fillMaxWidth().weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(color = Color.White)
                            .padding(20.dp)
                            .clickable{
                                context.startActivity(Intent(context, HealthScoreDetailed::class.java))
                            },
                        ) {
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)) {



                                Text(text="HEALTH SCORE",
                                    fontSize = 10.33.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.CenterHorizontally))

                                Spacer(modifier = Modifier.height(15.dp))

                                Text(text="WEEK 1",
                                    fontSize = 10.33.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Start))

                                var percentWeek1 by remember { mutableStateOf(0.35f) }
                                DynamicBar(progress = percentWeek1)

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(text="WEEK 2",
                                    fontSize = 10.33.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Start))

                                var percentWeek2 by remember { mutableStateOf(0.5f) }
                                DynamicBar(progress = percentWeek2)

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(text="WEEK 3",
                                    fontSize = 10.33.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Start))

                                var percentWeek3 by remember { mutableStateOf(0.8f) }
                                DynamicBar(progress = percentWeek3)

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(text="WEEK 4",
                                    fontSize = 10.33.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Start))

                                var percentWeek4 by remember { mutableStateOf(0.3f) }
                                DynamicBar(progress = percentWeek4)
                            }

                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        Box(modifier = Modifier
                            .fillMaxWidth().weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(color = Color.White)
                            .padding(20.dp),
                        ) {
                            Column(modifier = Modifier
                                .fillMaxWidth()) {

                                Text(
                                    text = "TIME-DOMAIN",
                                    fontSize = 10.33.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Start)
                                )
                                Text(
                                    text = "MEASURES",
                                    fontSize = 10.33.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Mean RR Interval: 80- ms",
                                    fontSize = 10.33.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Text(
                                    text = "Average heart rate is -75 bpm, which is within normal testing range: ",
                                    fontSize = 9.5.sp,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = "SDNN: 120 ms",
                                    fontSize = 10.33.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Text(
                                    text = "Overall heart rate variability is good, indicating a balanced autonomic nervous system. ",
                                    fontSize = 9.5.sp,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Box(
                                    modifier = Modifier
                                        .height(23.94.dp)
                                        .fillMaxWidth()
                                        .padding(horizontal = 22.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFFF0000))
                                        .clickable { },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth()
                                        , horizontalArrangement = Arrangement.Center) {
                                        Text(
                                            text="SEE MORE",
                                            color=Color.White,
                                            fontSize=8.sp
                                        )

                                        Spacer(modifier = Modifier.width(5.dp))

                                        Image(
                                            painter = painterResource(R.drawable.proceed_button),
                                            contentDescription = "SEE MORE BUTTON",
                                            modifier = Modifier.clickable {
                                                context.startActivity(Intent(context,
                                                    TimeDomainFeaturesActivity::class.java))
                                                activity.overridePendingTransition(
                                                    android.R.anim.fade_in,
                                                    android.R.anim.fade_out)
                                            }

                                        )
                                    }


                                }

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(modifier = Modifier
                        .height(170.7.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .fillMaxWidth()
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
                                    .fillMaxSize(),
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center
                                ) {

                                    Text(date, fontWeight = FontWeight.Bold)
                                    Text("LATEST WEEK", fontWeight = FontWeight.Bold)

                                    Spacer(modifier = Modifier.height(8.dp))
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

                                    Text(text = "INSIGHT", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(text = description, fontSize = 9.5.sp)



                                }
                            }

                            Spacer(modifier = Modifier.width(5.dp))

                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.65f)) {
                                LineGraph(data = heartRateData, predicted = null, modifier = Modifier.padding(start = 40.dp, top = 15.dp, bottom = 20.dp, end = 20.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(modifier = Modifier
                        .height(170.7.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .fillMaxWidth(),

                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)) {
                            Box(modifier = Modifier
                                .weight(0.35f)
                                .fillMaxWidth(),
                            ) {
                                Column(modifier = Modifier
                                    .fillMaxSize(),
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center
                                ) {

                                    Text(date, fontWeight = FontWeight.Bold)
                                    Text("LATEST WEEK", fontWeight = FontWeight.Bold)

                                    Spacer(modifier = Modifier.height(8.dp))
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

                                    Text(text = "FORECAST", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(text = description, fontSize = 9.5.sp)



                                }
                            }

                            Spacer(modifier = Modifier.width(5.dp))

                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.65f)) {
                                LineGraph(data = heartRateData, predicted = heartRatePredicted)
                            }
                        }
                    }
                }
            }

        }


}

@Composable
fun ExpandableBox(
    content: @Composable () -> Unit // Content passed as a lambda
) {
    var expanded by remember { mutableStateOf(false) } // Track expansion state

    // Animate height change smoothly
    val boxHeight by animateDpAsState(
        targetValue = if (expanded) 200.dp else 100.dp
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(boxHeight)
            .background(Color.LightGray)
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Column {
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                content() // Display the content passed in
            }
        }
    }
}


@Composable
fun DynamicBar(
    progress: Float,
    height: Dp = 16.dp,
    backgroundColor: Color = Color.LightGray,
    progressColor: Color = Color(0xFFFF0000),
    cornerRadius: Dp = 12.dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(cornerRadius))
                .background(progressColor)
        )
    }
}


@Composable
fun LineGraphSimple(data: List<Float>) {
    if (data.isEmpty()) return

    val maxY = data.maxOrNull() ?: 100f
    val minY = data.minOrNull() ?: 0f

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        val widthPerPoint = size.width / (data.size - 1)
        val normalized = data.map { (it - minY) / (maxY - minY) }

        val points = normalized.mapIndexed { index, fraction ->
            Offset(x = index * widthPerPoint, y = size.height * (1f - fraction))
        }

        // Draw line
        for (i in 0 until points.size - 1) {
            drawLine(color = Color.Red, start = points[i], end = points[i + 1], strokeWidth = 5f)
        }
    }
}

@Composable
fun LineGraph(
    data: List<Float>, // main data
    predicted: List<Float>? = null, // optional predicted data
    modifier: Modifier = Modifier.padding(start = 40.dp, top = 15.dp, bottom = 20.dp, end = 20.dp)
) {
    if (data.isEmpty()) return // nothing to draw

    val combined = if (predicted != null) data + predicted else data
    val maxY = combined.maxOrNull() ?: 100f
    val minY = combined.minOrNull() ?: 0f

    var animateProgress by remember(data) { mutableStateOf(0f) }
    val progress by animateFloatAsState(
        targetValue = animateProgress,
        animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
    )

    LaunchedEffect(data) {
        animateProgress = 0f
        animateProgress = 1f   // restart animation when new data arrives
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val maxPoints = 10                                                                                                // max points to draw for readability
        val step = (data.size / maxPoints.toFloat()).coerceAtLeast(1f)
        val sampledData = mutableListOf<Pair<Int, Float>>()
        var idx = 0f
        while (idx < data.size) {
            sampledData.add(idx.toInt() to data[idx.toInt()])
            idx += step
        }
        val widthPerPoint = if (sampledData.size > 1) size.width / (sampledData.size - 1) else size.width


        val heightPerUnit = size.height / (maxY - minY)

        val yStep = (maxY - minY) / 5f  // 5 horizontal lines
        for (i in 0..5) {
            val yValue = minY + i * yStep
            val yPos = size.height - (yValue - minY) * heightPerUnit
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, yPos),
                end = Offset(size.width, yPos),
                strokeWidth = 1f
            )
            drawContext.canvas.nativeCanvas.drawText(
                "${yValue.toInt()}",
                -40f,
                yPos + 8f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }
        // Draw X-axis labels
        val xLabels = 5
        for (i in 0 until xLabels) {
            val fraction = i / (xLabels - 1f)
            val xPos = size.width * fraction
            val dataIndex = (fraction * (data.size - 1)).toInt()
            drawContext.canvas.nativeCanvas.drawText(
                (dataIndex + 1).toString(),
                xPos,
                size.height + 30f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }




        // Draw main line safely
        if (data.size > 1) {
            val pointsData = sampledData.map { (i, value) ->
                Offset(
                    x = sampledData.indexOfFirst { it.first == i } * widthPerPoint,
                    y = size.height - (value - minY) * heightPerUnit
                )
            }


            val segmentCount = pointsData.size - 1
            val segmentProgress = progress * segmentCount
            val currentSegment = segmentProgress.toInt()
            val segmentFraction = segmentProgress - currentSegment

            for (i in 0 until segmentCount) {
                val start = pointsData[i]
                val end = pointsData[i + 1]

                val lineEnd = when {
                    i < currentSegment -> end
                    i == currentSegment -> Offset(
                        x = start.x + (end.x - start.x) * segmentFraction,
                        y = start.y + (end.y - start.y) * segmentFraction
                    )
                    else -> continue
                }

                drawLine(color = Color.Red, start = start, end = lineEnd, strokeWidth = 10f)
            }

        } else {
            // draw a single point if only one data
            val y = size.height - data[0] * heightPerUnit
            drawCircle(color = Color.Red, radius = 15f, center = Offset(size.width / 2, y))
        }

        // Draw predicted line safely
        predicted?.takeIf { it.size > 1 }?.let { predictedData ->
            val pointsPredicted = predictedData.mapIndexed { index, value ->
                Offset(
                    x = size.width * index / (predictedData.size - 1).coerceAtLeast(1),
                    y = size.height - (value - minY) * heightPerUnit
                )
            }
            val segmentCount = pointsPredicted.size - 1
            val segmentProgress = progress * segmentCount
            val currentSegment = segmentProgress.toInt()
            val segmentFraction = segmentProgress - currentSegment

            for (i in 0 until segmentCount) {
                val start = pointsPredicted[i]
                val end = pointsPredicted[i + 1]

                val lineEnd = when {
                    i < currentSegment -> end
                    i == currentSegment -> Offset(
                        x = start.x + (end.x - start.x) * segmentFraction,
                        y = start.y + (end.y - start.y) * segmentFraction
                    )
                    else -> continue
                }

                drawLine(color = Color.Gray, start = start, end = lineEnd, strokeWidth = 10f)
            }
        }
    }
}
