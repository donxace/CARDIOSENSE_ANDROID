package com.example.arduino

import com.example.arduino.api.RetrofitClient
import com.example.arduino.model.LoginResponse
import com.example.arduino.model.SignupResponse

import android.util.Log


import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response



import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp
import com.example.arduino.ui.theme.ArduinoTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(
                onLoginClick = { email, password ->
                    startActivity(Intent(this, HomeActivity::class.java))
                },
                onFacebookClick = {
                    Toast.makeText(this, "Facebook Login Clicked", Toast.LENGTH_SHORT).show()
                },
                onGoogleClick = {
                    Toast.makeText(this, "Google Login Clicked", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

fun onLoginClick(email: String, password: String, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitClient.instance.login(email, password)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()

                    // Navigate to MainActivity
                    val intent = Intent(context, HomeActivity::class.java)
                    context.startActivity(intent)
                    // Optionally finish LoginActivity if context is Activity
                    if (context is LoginActivity) {
                        context.finish()
                    }

                } else {
                    Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
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
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onFacebookClick: () -> Unit,   // <-- declare here
    onGoogleClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.orange_mine))
            .padding(16.dp)
            ,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {



            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 47.78.sp,
                    lineHeight = 48.sp // minimize spacing
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            )

            Text(
                text = "We're Glad to see you",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 21.15.sp,
                    lineHeight = 20.sp
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(66.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.White) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 49.dp), // set a valid height
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
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) {
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
                },

                modifier = Modifier.fillMaxWidth()
                    .defaultMinSize(minHeight = 49.dp),
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
                onClick = { onLoginClick(email, password, context) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
            ) {
                Text(
                    text = "Login",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 14.28.sp
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Forgot the password?",
                color = Color.White,
                fontSize = 12.08.sp,
                modifier = Modifier.clickable {
                    context.startActivity(
                        Intent(context, ForgotPasswordActivity::class.java)
                    )
                }
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

                Text("  Or Login With  ", fontSize = 12.08.sp, color = Color.White)

                Divider(
                    color = Color.White,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
            }




            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {


                Button(
                    onClick = onFacebookClick,
                    shape = RoundedCornerShape(10.dp),
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
                    shape = RoundedCornerShape(10.dp),
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



            val annotatedText = buildAnnotatedString {
                // Default text with custom font size and color
                withStyle(style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 15.38.sp,
                    color = Color.White
                ).toSpanStyle()) {
                    append("Don't have an account? ")
                }

                // Clickable "Sign up" with custom font size and color
                pushStringAnnotation(tag = "SIGN_UP", annotation = "signup")
                withStyle(style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 15.38.sp,
                    color = Color.White
                ).toSpanStyle()) {
                    append("Sign up")
                }
                pop()
            }

            Spacer(modifier = Modifier.height(30.dp))

            ClickableText(
                text = annotatedText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 15.38.sp,
                    color = Color.White
                ),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(tag = "SIGN_UP", start = offset, end = offset)
                        .firstOrNull()?.let {
                            context.startActivity(Intent(context, SignUpActivity::class.java))
                        }
                }
            )
        }
    }
}