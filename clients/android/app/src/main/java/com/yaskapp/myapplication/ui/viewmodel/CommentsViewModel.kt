package com.yaskapp.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaskapp.myapplication.data.model.CommentAuthorDto
import com.yaskapp.myapplication.data.model.CommentDto
import com.yaskapp.myapplication.data.repository.PollsRepository
import com.yaskapp.myapplication.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import java.util.UUID

class CommentsViewModel : ViewModel() {

    private val repository = PollsRepository()

    var comments by mutableStateOf<List<CommentDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var input by mutableStateOf("")

    fun onInputChange(value: String) {
        input = value
    }

    fun loadComments(pollId: String) {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                comments = repository.getComments(pollId)
            } catch (e: Exception) {
                error = e.message ?: "Failed to load comments"
            } finally {
                isLoading = false
            }
        }
    }

    fun addComment(pollId: String) {
        val text = input.trim()
        if (text.isBlank()) {
            error = "Введите комментарий"
            return
        }

        val currentUser = SupabaseProvider.client.auth.currentUserOrNull()
        if (currentUser == null) {
            error = "Пользователь не авторизован"
            return
        }

        val oldComments = comments

        val optimisticComment = CommentDto(
            id = "temp-${UUID.randomUUID()}",
            content = text,
            createdAt = "",
            author = CommentAuthorDto(
                id = currentUser.id,
                username = currentUser.email?.substringBefore("@") ?: "user",
                displayName = currentUser.userMetadata?.get("display_name")?.toString()
                    ?.replace("\"", "")
                    ?: currentUser.email?.substringBefore("@")
                    ?: "User",
                avatarUrl = null
            )
        )

        comments = comments + optimisticComment
        input = ""
        error = null

        viewModelScope.launch {
            try {
                repository.addComment(pollId, text)
                comments = repository.getComments(pollId)
            } catch (e: Exception) {
                comments = oldComments
                input = text
                error = e.message ?: "Failed to add comment"
            }
        }
    }
}