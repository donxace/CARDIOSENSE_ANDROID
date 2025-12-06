package com.example.arduino

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForgotPasswordScreen(
                onProceedClick = { email ->
                    // Here you can trigger sending reset link
                    Toast.makeText(this, "Password reset link sent to $email", Toast.LENGTH_SHORT).show()
                },
                onLoginClick = {
                    // Navigate back to login
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            )
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    onProceedClick: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.orange_mine)) // same orangeMine color
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.group_1), // your vector drawable
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp).align(Alignment.Start),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "We will share a password reset link on your email address",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 13.93.sp,
                    lineHeight = 20.sp
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.White) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(49.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White.copy(alpha = 0.5f)
                ),
                textStyle = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onProceedClick(email) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text("Proceed")
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row {
                Text("Try logging in? ", color = Color.White)
                Text(
                    text = "Login Now",
                    color = Color.White,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}
