package com.example.arduino.api

import com.example.arduino.model.LoginResponse
import com.example.arduino.model.SignupResponse
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = " https://tiringly-zealous-brayan.ngrok-free.dev" // your PC IP


object RetrofitClient {

    // Create a lenient Gson instance
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Retrofit instance
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }

    // ✅ Do NOT call signup here. Call it from your Activity/Composable:
    // RetrofitClient.instance.signup(name, email, password)
}
