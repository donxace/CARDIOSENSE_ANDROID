package com.example.arduino

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

class HealthScoreDetailed : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dashboard {
                HealthScoreDetailedScreen()
            }
        }
    }
}

@Composable
fun HealthScoreDetailedScreen() {

    val context = LocalContext.current

    val activity = context as Activity
    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.orange_mine)),
        contentAlignment = Alignment.Center,

    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "HEALTH SCORE (DETAILED)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Image(
                        painter = painterResource(R.drawable.back_button),
                        contentDescription = "Back Button",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                context.startActivity(Intent(context,
                                    HealthScoreActivity::class.java))
                                        activity.overridePendingTransition(
                                            android.R.anim.fade_in,
                                            android.R.anim.fade_out)
                            }
                    )
                }
            }

            Spacer(modifier =Modifier.height(10.dp))
            Box(modifier = Modifier
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color.White)
                .padding(20.dp)
                .clickable{

                },
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(0.5f)) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "RMSSD",
                                    fontSize = 10.33.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                var percentWeek3 by remember { mutableStateOf(0.35f) }
                                DynamicBar(progress = percentWeek3)

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Ace's average RR interval corresponds to " +
                                            "a resting HR of about 73BPM, which is within normal range",
                                    fontSize = 9.33.sp,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "NN50 / pNN50",
                                    fontSize = 10.33.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                var percentWeek2 by remember { mutableStateOf(0.35f) }
                                DynamicBar(progress = percentWeek2)

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Ace's average RR interval corresponds to " +
                                            "a resting HR of about 73BPM, which is within normal range",
                                    fontSize = 9.33.sp,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Heart Rate Variability",
                                    fontSize = 10.33.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                var percentWeek4 by remember { mutableStateOf(0.35f) }
                                DynamicBar(progress = percentWeek4)

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Ace's average RR interval corresponds to " +
                                            "a resting HR of about 73BPM, which is within normal range",
                                    fontSize = 9.33.sp,
                                    modifier = Modifier.align(Alignment.Start)
                                )
                            }
                        }

                    Spacer(modifier = Modifier.width(20.dp))


                    Box(modifier = Modifier.weight(0.5f)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "MEAN RR INTERVAL: 820MS",
                                fontSize = 10.33.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            var percentWeek3 by remember { mutableStateOf(0.35f) }
                            DynamicBar(progress = percentWeek3)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Ace's average RR interval corresponds to " +
                                        "a resting HR of about 73BPM, which is within normal range",
                                fontSize = 9.33.sp,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "HEART RATE (BPM): 73 BPM",
                                fontSize = 10.33.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            var percentWeek2 by remember { mutableStateOf(0.35f) }
                            DynamicBar(progress = percentWeek2)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Ace's average RR interval corresponds to " +
                                        "a resting HR of about 73BPM, which is within normal range",
                                fontSize = 9.33.sp,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "SDNN: 52MS",
                                fontSize = 10.33.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            var percentWeek4 by remember { mutableStateOf(0.35f) }
                            DynamicBar(progress = percentWeek4)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Ace's average RR interval corresponds to " +
                                        "a resting HR of about 73BPM, which is within normal range",
                                fontSize = 9.33.sp,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()) {
                    Text("OVERALL HEART HEALTH SCORE", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    DynamicBar(progress = 0.35f, height = 30.dp)
                }
            }
        }
    }
}
