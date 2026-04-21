package com.yaskapp.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaskapp.myapplication.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var username by mutableStateOf("")
        private set
    var displayName by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var isLoggedIn by mutableStateOf(repository.isLoggedIn())
        private set

    fun onEmailChange(value: String) {
        email = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    fun onUsernameChange(value: String) {
        username = value
    }

    fun onDisplayNameChange(value: String) {
        displayName = value
    }

    fun clearError() {
        error = null
    }

    fun signIn() {
        val safeEmail = email.trim()
        val safePassword = password.trim()

        if (safeEmail.isBlank()) {
            error = "Введите email"
            return
        }

        if (safePassword.isBlank()) {
            error = "Введите пароль"
            return
        }

        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                repository.signIn(safeEmail, safePassword)
                isLoggedIn = true
            } catch (e: Exception) {
                error = e.message ?: "Ошибка входа"
            } finally {
                isLoading = false
            }
        }
    }

    fun signUp() {
        val safeEmail = email.trim()
        val safePassword = password.trim()
        val safeUsername = username.trim()
        val safeDisplayName = displayName.trim().ifBlank { safeUsername }

        if (safeEmail.isBlank()) {
            error = "Введите email"
            return
        }

        if (safePassword.isBlank()) {
            error = "Введите пароль"
            return
        }

        if (safeUsername.isBlank()) {
            error = "Введите username"
            return
        }

        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                repository.signUp(
                    email = safeEmail,
                    password = safePassword,
                    username = safeUsername,
                    displayName = safeDisplayName
                )
                isLoggedIn = repository.isLoggedIn()
                if (!isLoggedIn) {
                    error = "Аккаунт создан. Теперь войдите."
                }
            } catch (e: Exception) {
                error = e.message ?: "Ошибка регистрации"
            } finally {
                isLoading = false
            }
        }
    }
}

