package com.example.arduino.model

data class LoginResponse(
    val status: String,
    val message: String? = null,
    val token: String? = null
)