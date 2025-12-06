package com.example.arduino.api

import com.example.arduino.model.LoginResponse
import com.example.arduino.model.SignupResponse
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.22/heart_sense/" // your PC IP

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
