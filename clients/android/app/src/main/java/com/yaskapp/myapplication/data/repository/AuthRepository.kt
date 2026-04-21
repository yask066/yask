package com.yaskapp.myapplication.data.repository

import com.yaskapp.myapplication.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {

    suspend fun signUp(
        email: String,
        password: String,
        username: String,
        displayName: String
    ) {
        SupabaseProvider.client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("username", username)
                put("display_name", displayName)
            }
        }
    }

    suspend fun signIn(
        email: String,
        password: String
    ) {
        SupabaseProvider.client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        SupabaseProvider.client.auth.signOut()
    }

    fun currentUserId(): String? {
        return SupabaseProvider.client.auth.currentUserOrNull()?.id
    }

    fun isLoggedIn(): Boolean {
        return SupabaseProvider.client.auth.currentSessionOrNull() != null
    }
}

