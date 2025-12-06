package com.example.arduino


import com.example.arduino.api.RetrofitClient
import com.example.arduino.model.SignupResponse
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContent {
                SignUpScreen(
                    onSignUpClick = { name, email, password ->
                         startActivity(Intent(this, LoginActivity::class.java))
                    },
                    onLoginClick = {
                        startActivity(Intent(this, LoginActivity::class.java))
                    },
                    onForgotPasswordClick = {
                        startActivity(Intent(this, ForgotPasswordActivity::class.java))
                    },
                    onFacebookClick = {
                        Toast.makeText(this, "Facebook Login Clicked!", Toast.LENGTH_SHORT).show()
                    },
                    onGoogleClick = {
                        Toast.makeText(this, "Google Login Clicked!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
    }
}

fun onSignUpClick(name: String, email: String, password: String, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitClient.instance.signup(name, email, password)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, "Signup Successful", Toast.LENGTH_SHORT).show()

                    // Navigate to LoginActivity
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    if (context is SignUpActivity) context.finish()
                } else {
                    Toast.makeText(context, "Signup Failed: ${response.body()?.reason}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun SignUpScreen(
    onSignUpClick: (String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onGoogleClick: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("")}
    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("")}
    var passwordVisible by remember { mutableStateOf(false)}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.orange_mine))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.group_1), // your vector drawable
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp).align(Alignment.Start),
                contentScale = ContentScale.Fit
            )

            Text(text = "Create your account", style = MaterialTheme.typography.headlineMedium, color = Color.White, modifier = Modifier.align(Alignment.Start))

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name", color = Color.White) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(49.dp), // set a valid height
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White.copy(alpha = 0.5f)
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address", color = Color.White) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(49.dp), // set a valid height
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White.copy(alpha = 0.5f)
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Type a password", color = Color.White) },
                singleLine = true,

                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(49.dp),

                visualTransformation = if(passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

                trailingIcon = {
                    val icon = if (passwordVisible)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible}) {
                        Icon(imageVector = icon, contentDescription = "TogglePassword")
                    }
                },// set a valid height

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White.copy(alpha = 0.5f)
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onSignUpClick(name, email, password, context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Forgot the password?",
                color = Color.White,
                modifier = Modifier.clickable { onForgotPasswordClick() }
            )

            Spacer(modifier = Modifier.height(25.dp))

            Row(
                verticalAlignment =  Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(
                    color = Color.White,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )

                Text("  Or Sign Up With  ", fontSize = 12.08.sp, color = Color.White)

                Divider(
                    color = Color.White,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onFacebookClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.facebook_logo), // make sure you have this drawable
                        contentDescription = "Facebook Logo",
                        modifier = Modifier.size(35.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onGoogleClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gmail_logo), // make sure you have this drawable
                        contentDescription = "Facebook Logo",
                        modifier = Modifier.size(35.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row {
                Text("Already have an account? ", color = Color.White)
                Text(
                    text = "Login Now",
                    color = Color.White,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }


}