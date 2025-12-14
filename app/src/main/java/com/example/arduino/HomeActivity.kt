package com.example.arduino

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var date = "Dec 8, 2025"

        super.onCreate(savedInstanceState)
        setContent {
            Dashboard {
                HomeActivityScreen()
            }
        }
    }
}

@Composable
fun HomeActivityScreen() {
    val scrollState = rememberScrollState()
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


        ArcProgressScreen(progress = 0.75f)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "ACTIVITIES",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Start),
        )

        Spacer(modifier = Modifier.height(10.dp))

        ActivityLog()

        Spacer(modifier = Modifier.height(7.dp))
        ActivityLog()

        Spacer(modifier = Modifier.height(7.dp))

        ActivityLog()

    }
}

@Composable
fun ActivityLog() {
    Box(modifier = Modifier
        .height(150.7.dp)
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
                    .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(text = "MORNING TEST", fontWeight = FontWeight.Bold)
                    Text("9:12 AM", fontWeight = FontWeight.Bold)

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

                    Text(text = "HEART METRICS", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxHeight()
                            .weight(0.5f)
                        ) {
                            Column(modifier = Modifier
                                .fillMaxWidth()) {
                                Text("BPM", fontSize = 9.sp)
                                Text("80", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        Box(modifier = Modifier.fillMaxHeight()
                            .weight(0.5f)
                        ) {
                            Column(modifier = Modifier
                                .fillMaxWidth()) {
                                Text("HRV", fontSize = 9.sp)
                                Text("99", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }



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