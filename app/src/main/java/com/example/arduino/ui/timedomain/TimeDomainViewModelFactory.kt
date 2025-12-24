package com.example.arduino.ui.timedomain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.arduino.data.repository.SessionRepository

class TimeDomainViewModelFactory(
    private val repository: SessionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeDomainViewModel::class.java)) {
            return TimeDomainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
