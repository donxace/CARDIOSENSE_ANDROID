package com.example.arduino.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arduino.arduinoManager

@Composable
fun ConnectionIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp) // top-right corner padding
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            // Status text
            Text(
                text = if (arduinoManager.isConnected) "Connected" else "Disconnected",
                fontSize = 10.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(6.dp)) // space between text and circle

            // Circle indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = if (arduinoManager.isConnected) Color.Green else Color.Red,
                        shape = CircleShape
                    )
            )
        }
    }
}