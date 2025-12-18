package com.example.arduino

import android.app.Application
import com.example.arduino.data.AppDatabase

class ArduinoApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
