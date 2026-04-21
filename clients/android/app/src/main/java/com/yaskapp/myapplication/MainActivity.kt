package com.yaskapp.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.yaskapp.myapplication.ui.auth.AuthRoute
import com.yaskapp.myapplication.ui.navigation.MainScaffold

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val authViewModel = remember { AuthViewModel() }

            if (authViewModel.isLoggedIn) {
                MainScaffold()
            } else {
                AuthRoute(authViewModel)
            }
        }
    }
}