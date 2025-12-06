package com.example.arduino.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.arduino.R  // Make sure your R import is correct

// 1️⃣ Define Montserrat FontFamily
val montserratFont = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_bold, FontWeight.Bold)
)

// 2️⃣ Define Typography using Montserrat
val ArduinoTypography  = Typography(
    bodyLarge = TextStyle(
        fontFamily = montserratFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = montserratFont,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )
)