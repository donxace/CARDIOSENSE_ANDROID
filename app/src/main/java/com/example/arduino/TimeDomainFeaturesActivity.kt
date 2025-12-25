package com.example.arduino

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.remote.creation.toFloat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.arduino.data.AppDatabase
import com.example.arduino.data.SessionMetricsDao
import com.example.arduino.data.SessionMetricsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.arduino.data.*
import com.example.arduino.data.repository.SessionRepository
import com.example.arduino.ui.timedomain.TimeDomainViewModel
import com.example.arduino.ui.timedomain.TimeDomainViewModelFactory
import com.example.arduino.domain.SessionAverages

class TimeDomainFeaturesActivity : ComponentActivity() {

    private lateinit var viewModel: TimeDomainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize DAO and Repository
        val dao = AppDatabase.getDatabase(this).sessionMetricsDao()
        val repository = SessionRepository(dao)
        val db = AppDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.logAndGetDailyAverages(1000)
            //insertFakeSessionsForNext20Days(db)
        }

        // Initialize ViewModel using factory
        val factory = TimeDomainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[TimeDomainViewModel::class.java]

        setContent {
            Dashboard {
                TimeDomainFeaturesScreen(viewModel = viewModel)
            }
        }
    }




    // Function to log session ranges
    private fun logSessionMetrics(ranges: SessionTimeRanges) {
        Log.d("SessionMetrics", "=== Last 24 Hours ===")
        ranges.last24Hours.forEach { logSession(it) }

        Log.d("SessionMetrics", "=== Last Week ===")
        ranges.lastWeek.forEach { logSession(it) }

        Log.d("SessionMetrics", "=== Last Year ===")
        ranges.lastYear.forEach { logSession(it) }
    }

    private fun logSession(session: SessionMetricsEntity) {
        Log.d(
            "SessionMetrics",
            "Time: ${session.sessionStartTime}, BPM: ${session.bpm}, RR: ${session.avgRR}"
        )
    }
}



var meanRRData = listOf(30f, 50f, 45f, 55f, 60f, 35f, 55.5f)

@Composable
fun TimeDomainFeaturesScreen(viewModel: TimeDomainViewModel) {

    var selectedRange by remember { mutableStateOf("24h") }
    var averages by remember { mutableStateOf<SessionAverages?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var metricsList by remember { mutableStateOf<List<SessionMetricsEntity>>(emptyList()) }



    fun loadAverages(range: String) {
        coroutineScope.launch {
            selectedRange = range
            averages = when(range) {
                "24h" -> viewModel.last24HoursAverages()
                "14d" -> viewModel.last14DaysAverages()
                "30d" -> viewModel.last30DaysAverages()
                else -> viewModel.last24HoursAverages()
            }

            metricsList = when(range) {
                "24h" -> viewModel.last24HoursMetrics()
                "14d" -> viewModel.last14DaysMetrics()
                "30d" -> viewModel.last30DaysMetrics()
                else -> viewModel.last24HoursMetrics()
            }

            Log.d("SessionMetrics", "Loaded $range averages: $averages")
            Log.d("SessionMetricsAverages", "Loaded $range metrics: ${metricsList.map { it.avgRR }}")
            Log.d("SessionMetrics", "METRICS: {$metricsList}")
        }
    }

    val rmssdList by remember(metricsList) { mutableStateOf(metricsList.map { it.rmssd }) }
    val nn50List by remember(metricsList) { mutableStateOf(metricsList.map { it.nn50.toFloat() }) }
    val rrList   by remember(metricsList) { mutableStateOf(metricsList.map { it.avgRR }) }
    val bpmList  by remember(metricsList) { mutableStateOf(metricsList.map { it.bpm }) }
    val sdnnList by remember(metricsList) { mutableStateOf(metricsList.map { it.sdnn }) }




    // LaunchedEffect to call suspend functions once
    LaunchedEffect(Unit) {
        loadAverages("24h")
        // 24 hours
        val avg24h = viewModel.last24HoursAverages()
        Log.d("SessionMetrics", "=== Last 24 Hours ===")
        Log.d("SessionMetrics", "Avg BPM: ${avg24h.avgBPM}")
        Log.d("SessionMetrics", "Avg SDNN: ${avg24h.avgSDNN}")
        Log.d("SessionMetrics", "Avg RMSSD: ${avg24h.avgRMSSD}")
        Log.d("SessionMetrics", "Avg NN50: ${avg24h.avgNN50}")
        Log.d("SessionMetrics", "Avg PNN50: ${avg24h.avgPNN50}")
        Log.d("SessionMetrics", "Avg RR: ${avg24h.avgRR}")

        // 14 days
        val avg14d = viewModel.last14DaysAverages()
        Log.d("SessionMetrics", "=== Last 14 Days ===")
        Log.d("SessionMetrics", "Avg BPM: ${avg14d.avgBPM}")
        Log.d("SessionMetrics", "Avg SDNN: ${avg14d.avgSDNN}")
        Log.d("SessionMetrics", "Avg RMSSD: ${avg14d.avgRMSSD}")
        Log.d("SessionMetrics", "Avg NN50: ${avg14d.avgNN50}")
        Log.d("SessionMetrics", "Avg PNN50: ${avg14d.avgPNN50}")
        Log.d("SessionMetrics", "Avg RR: ${avg14d.avgRR}")
    }

    val context = LocalContext.current
    val activity = context as Activity
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(colorResource(R.color.orange_mine))
            .padding(15.dp)
    ) {

        // ---------------- HEADER ----------------
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TIME DOMAIN FEATURES",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.width(50.dp))

                Image(
                    painter = painterResource(R.drawable.back_button),
                    contentDescription = "Back Button",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            context.startActivity(Intent(context, HealthScoreActivity::class.java))
                            activity.overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            )
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selectedRange == "24h") Color.LightGray else Color.White)
                        .weight(1f)
                        .padding(10.dp)
                        .clickable {
                            loadAverages("24h")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Last 24 Hours", fontWeight = FontWeight.ExtraBold)
                }

                Spacer(modifier = Modifier.width(5.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selectedRange == "14d") Color.LightGray else Color.White)
                        .weight(1f)
                        .padding(10.dp)
                        .clickable {
                            loadAverages("14d")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("14 Days", fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ---------------- FIRST BOX ----------------'


        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .fillMaxWidth()
                .height(250.dp)
                .padding(20.dp)
        ) {
            Column {
                Text("RMSSD: ${averages?.avgRMSSD ?: 0f}", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("Standard Deviation of RR intervals", fontSize = 9.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("WEEK 4", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                LineGraph(rmssdList)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .fillMaxWidth()
                .height(250.dp)
                .padding(20.dp)
        ) {
            Column {
                Text("NN50 / PNN50: ${averages?.avgNN50 ?: 0f}", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("The short-term HRV measure", fontSize = 9.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("WEEK 4", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                LineGraph(data = nn50List, predicted = null)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .fillMaxWidth()
                .height(250.dp)
                .padding(20.dp)
        ) {
            Column {
                Text("RR Interval: ${averages?.avgRMSSD ?: 0f}", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("The short-term HRV measure", fontSize = 9.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("WEEK 4", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                LineGraph(data = rrList, predicted = null)
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .fillMaxWidth()
                .height(250.dp)
                .padding(20.dp)
        ) {
            Column {
                Text("BEATS PER MINUTE: ${averages?.avgBPM ?: 0f}", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("The short-term HRV measure", fontSize = 9.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("WEEK 4", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                LineGraph(data = bpmList, predicted = null)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .fillMaxWidth()
                .height(250.dp)
                .padding(20.dp)
        ) {
            Column {
                Text("SDNN: ${averages?.avgSDNN ?: 0f}", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("The short-term HRV measure", fontSize = 9.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("WEEK 4", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                LineGraph(data = sdnnList, predicted = null)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box( modifier = Modifier
                    .weight(0.2f),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.download_button_svgrepo_com),
                        contentDescription = "Back Button",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {},
                        colorFilter = ColorFilter.tint(Color.LightGray)

                    )
                }

            }
        }
    }
}
