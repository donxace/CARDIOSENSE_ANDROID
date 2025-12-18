package com.example.arduino

import android.content.Intent
import androidx.compose.ui.geometry.Offset
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.example.arduino.DashboardActivity.PieChartData
import kotlin.collections.forEach
import android.app.Activity
import kotlin.math.roundToInt


data class BarData(
    val hrv: Float,
    val bpm: Float
)

val weeklyData = listOf(
    BarData(hrv = 20f, bpm = 70f),
    BarData(hrv = 25f, bpm = 72f),
    BarData(hrv = 18f, bpm = 68f),
    BarData(hrv = 22f, bpm = 71f)
)

class DashboardActivity : ComponentActivity() {
    data class PieChartData(
        val value: Float,
        val color: Color,
        val label: String
    )

val heartRateData = listOf(10f, 40f, 67f, 78f)

    val pieData = listOf(
        PieChartData(20f, Color(0XFF6E4648), "Monday"),
        PieChartData(10f, Color(0XFFFF9C9C), "Tuesday"),
        PieChartData(30f, Color(0XFFF6391E), "Wednesday"),
        PieChartData(25f, Color(0XFF4B0900), "Thursday"),
        PieChartData(15f, Color(0XFF000000), "Friday")
    )



    override fun onCreate(savedInstanceState : Bundle? ) {
        super.onCreate(savedInstanceState)
        setContent {
            Dashboard {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

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

                    //AVERAGE BPM DASHBOARD

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .height(233.dp)
                            .background(Color.White)
                    ) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                        ) {
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)) {
                                Text("AVERAGE BPM", fontSize = 12.2.sp, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(10.dp))

                                BarGraph(
                                    data = weeklyData,
                                    modifier = Modifier.fillMaxWidth().height(250.dp)
                                )
                            }
                        }

                    }


                    Spacer(modifier = Modifier.height(10.dp))

                    // summarization

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()

                            .height(133.dp)
                    ) {
                        Row() {
                            Box(Modifier.fillMaxSize()
                                .weight(1f)
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.White))  {
                                Column(modifier = Modifier.fillMaxSize()
                                    .padding(20.dp))  {
                                    Text("SUMMARIZATION", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("+95.1", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Leads grew from 200 in January to 400 in May. This shows that lead generation is improving.", fontSize = 9.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(5.dp))

                            Box(Modifier.fillMaxSize()
                                .weight(1f)
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.White))  {
                                Column(modifier = Modifier.fillMaxSize()
                                    .padding(20.dp))  {
                                    Text("RECCOMMENDATION", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("+98.6", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Leads grew from 200 in January to 400 in May. This shows that lead generation is improving.", fontSize = 9 .sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
// Header Composable
@Composable
fun Header(title: String) {
    val textSize = 24.sp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.header_mine))
            .padding(30.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.group_1), // your vector drawable
                contentDescription = "App Logo",
                modifier = Modifier
                    .height(textSize.value.dp)
                    .aspectRatio(1f)
            )

            Text(
                text = title,
                fontSize = 30.1.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

    }
}

// Footer Composable (optional)
@Composable
fun Footer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as Activity

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(R.color.header_mine))
            .padding(horizontal = 5.dp),
        contentAlignment = Alignment.Center // centers the text inside the Box
    ) {

        Row(
            modifier = Modifier
                .padding(25.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_button), // your vector drawable
                contentDescription = "App Logo",
                modifier = Modifier.size(40.dp)
                    .clickable{
                        context.startActivity(
                            Intent(context, HomeActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    },
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White)
            )

            Spacer(modifier = Modifier.width(170.dp))

            Image(
                painter = painterResource(id = R.drawable.target_logo), // your vector drawable
                contentDescription = "App Logo",
                modifier = Modifier.size(40.dp)
                    .clickable{
                        context.startActivity(Intent(context, HealthScoreActivity::class.java))
                        activity.overridePendingTransition(
                            android.R.anim.fade_in, android.R.anim.fade_out)
                    },
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

        FloatingActionButton(
            onClick = { context.startActivity(Intent(context, TimerActivity::class.java))},
            shape = CircleShape,
            containerColor = Color(0xFF9A2919),
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-40).dp)
        ) {
            Text("+", color = Color.White, fontSize = 22.1.sp)
        }
    }
}


// Dashboard screen using Header
@Composable
fun Dashboard(
    bodyContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.orange_mine))
    ) {

        // HEADER
        Header(" CARDIOSENSE")

        // CONTENT AREA (Editable)
        Box(
            modifier = Modifier
                .weight(1f)          // takes all space between header & footer
                .fillMaxWidth()
                .padding(5.dp)

        ) {
            bodyContent()        // << your editable content goes here
        }

        // FOOTER
        Footer()
    }
}

@Composable
fun PieChart(
    data: List<PieChartData>,
    chartSize: Dp = 100.dp,       // overall PieChart size
    innerCircleSize: Dp = 40.dp    // exact inner circle (hole) size
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat()

    Canvas(modifier = Modifier.size(chartSize).aspectRatio(1f)) {
        val radius = size.minDimension / 2
        val centerX = size.width / 2
        val centerY = size.height / 2

        val innerPx = innerCircleSize.toPx()

        // Stroke width = outer radius - inner radius
        val strokeWidth = (radius * 2 - innerPx).coerceAtLeast(1f)

        var startAngle = -90f

        data.forEach { slice ->
            val sweepAngle = (slice.value / total) * 360f

            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
            )

            startAngle += sweepAngle
        }
    }
}

@Composable
fun BarGraph(
    data: List<BarData>,
    modifier: Modifier = Modifier,
    yAxisSteps: Int = 5,
    chartHeight: Dp = 140.dp,
    barWidth: Dp = 33.dp,
    spacingBetweenWeeks: Dp = 10.dp
) {
    val maxValue = (data.maxOf { maxOf(it.hrv, it.bpm) } * 1.2f)

    Row(modifier = modifier) {
        // Y-axis labels
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(chartHeight)
        ) {
            for (i in yAxisSteps downTo 0) {
                val label = ((maxValue / yAxisSteps) * i).roundToInt()
                Text(text = "$label", fontSize = 12.sp, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Chart + X-axis labels
        Column {
            // Chart area
            Box(modifier = Modifier.height(chartHeight)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw horizontal gridlines
                    for (i in 0..yAxisSteps) {
                        val y = size.height - (i.toFloat() / yAxisSteps) * size.height
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f
                        )
                    }

                    // Draw bars
                    val totalWeeks = data.size
                    val spacePerWeek = (size.width - spacingBetweenWeeks.toPx() * (totalWeeks - 1)) / totalWeeks

                    data.forEachIndexed { index, item ->
                        val hrvHeight = (item.hrv / maxValue) * size.height
                        val bpmHeight = (item.bpm / maxValue) * size.height

                        val startX = index * (spacePerWeek + spacingBetweenWeeks.toPx())

                        // HRV bar
                        drawRect(
                            color = Color(0XFF8C2327),
                            topLeft = Offset(startX, size.height - hrvHeight),
                            size = androidx.compose.ui.geometry.Size(barWidth.toPx(), hrvHeight)
                        )

                        // BPM bar
                        drawRect(
                            color = Color(0XFFEE0810),
                            topLeft = Offset(startX + barWidth.toPx(), size.height - bpmHeight),
                            size = androidx.compose.ui.geometry.Size(barWidth.toPx(), bpmHeight)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // X-axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacingBetweenWeeks)
            ) {
                data.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier.width(barWidth * 2),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Week ${index + 1}", fontSize = 12.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}

