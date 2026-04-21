package com.yaskapp.myapplication.ui.auth

import androidx.compose.runtime.*
import com.yaskapp.myapplication.AuthViewModel

private enum class AuthMode {
    LOGIN,
    REGISTER
}

@Composable
fun AuthRoute(viewModel: AuthViewModel) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }

    when (authMode) {
        AuthMode.LOGIN -> LoginScreen(
            viewModel = viewModel,
            onOpenRegister = {
                viewModel.clearError()
                authMode = AuthMode.REGISTER
            }
        )

        AuthMode.REGISTER -> RegisterScreen(
            viewModel = viewModel,
            onOpenLogin = {
                viewModel.clearError()
                authMode = AuthMode.LOGIN
            }
        )
    }
}

