package com.yaskapp.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaskapp.myapplication.data.model.PollDto
import com.yaskapp.myapplication.data.model.ProfileDto
import com.yaskapp.myapplication.data.repository.PollsRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = PollsRepository()

    var profile by mutableStateOf<ProfileDto?>(null)
        private set

    var polls by mutableStateOf<List<PollDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadProfile() {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                profile = repository.getMyProfile()
                polls = repository.getMyPolls()
            } catch (e: Exception) {
                error = e.message ?: "Failed to load profile"
            } finally {
                isLoading = false
            }
        }
    }
}

