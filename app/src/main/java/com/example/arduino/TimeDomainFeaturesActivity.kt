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

class TimeDomainFeaturesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dashboard {
                TimeDomainFeaturesScreen()
            }
        }
    }
}

var meanRRData = listOf(30f, 50f, 45f, 55f, 60f, 35f, 55f)

@Composable
fun TimeDomainFeaturesScreen() {
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
                                android.R.anim.fade_out)
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
                        .background(Color.White)
                        .weight(1f)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Last 24 Hours", fontWeight = FontWeight.ExtraBold)
                }

                Spacer(modifier = Modifier.width(5.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .weight(1f)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Last 14 Days", fontWeight = FontWeight.ExtraBold)
                }

                Spacer(modifier = Modifier.width(5.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .weight(1f)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Last 30 Days", fontWeight = FontWeight.ExtraBold)
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
                Text("RMSSD: 20MS", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("Standard Deviation of RR intervals", fontSize = 9.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("WEEK 4", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                LineGraph(data = meanRRData, predicted = null)
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
                Text("NN50 / PNN50: 20MS", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("The short-term HRV measure", fontSize = 9.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("WEEK 4", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                LineGraph(data = meanRRData, predicted = null)
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
                Text("Heart Rate Variability: 20MS", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("Standard deviation of RR intervals", fontSize = 9.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("WEEK 4", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                LineGraph(data = meanRRData, predicted = null)
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
                Text("RR Interval: 20MS", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("The short-term HRV measure", fontSize = 9.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("WEEK 4", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                LineGraph(data = meanRRData, predicted = null)
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
                Text("BEATS PER MINUTE: 20MS", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("The short-term HRV measure", fontSize = 9.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("WEEK 4", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                LineGraph(data = meanRRData, predicted = null)
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
                Text("SDNN: 20MS", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("The short-term HRV measure", fontSize = 9.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("WEEK 4", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                LineGraph(data = meanRRData, predicted = null)
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
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
