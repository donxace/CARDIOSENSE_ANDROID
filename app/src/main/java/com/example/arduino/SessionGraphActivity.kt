package com.example.arduino

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class SessionGraphActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the RR data passed from the previous Activity
        val rrArray = intent.getFloatArrayExtra("RR_DATA") ?: floatArrayOf()
        val rrList = rrArray.toList() // convert to List<Float>

        setContent {
            MaterialTheme {
                SessionGraphScreen(rrData = rrList)
            }
        }
    }
}

@Composable
fun SessionGraphScreen(rrData: List<Float>) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Text(
            text = "Session Graph",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pass the rrData to your existing LineGraph
        LineGraph(
            data = rrData,
            predicted = null, // or some prediction if needed
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )
    }
}
