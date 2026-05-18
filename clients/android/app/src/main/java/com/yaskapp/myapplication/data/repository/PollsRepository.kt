package com.yaskapp.myapplication.data.repository

import com.yaskapp.myapplication.data.model.CommentDto
import com.yaskapp.myapplication.data.model.PollDto
import com.yaskapp.myapplication.data.model.ProfileDto
import com.yaskapp.myapplication.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class PollsRepository {

    private val client = SupabaseProvider.client

    suspend fun getFeed(): List<PollDto> {
        val currentUserId = client.auth.currentUserOrNull()?.id

        return client.postgrest
            .rpc(
                function = "get_feed",
                parameters = buildJsonObject {
                    if (currentUserId == null) {
                        put("p_user_id", JsonNull)
                    } else {
                        put("p_user_id", currentUserId)
                    }
                    put("p_limit", 20)
                    put("p_offset", 0)
                }
            )
            .decodeList<PollDto>()
    }

    suspend fun vote(optionId: String) {
        val currentUserId = client.auth.currentUserOrNull()?.id
            ?: error("Пользователь не авторизован")

        client.postgrest
            .rpc(
                function = "vote",
                parameters = buildJsonObject {
                    put("p_user_id", currentUserId)
                    put("p_option_id", optionId)
                }
            )
    }

    suspend fun toggleLike(pollId: String) {
        val currentUserId = client.auth.currentUserOrNull()?.id
            ?: error("Пользователь не авторизован")

        client.postgrest
            .rpc(
                function = "toggle_like",
                parameters = buildJsonObject {
                    put("p_user_id", currentUserId)
                    put("p_poll_id", pollId)
                }
            )
    }

    suspend fun createPoll(
        question: String,
        options: List<String>
    ) {
        client.postgrest.rpc(
            function = "create_poll",
            parameters = buildJsonObject {
                put("p_question", question)
                put(
                    "p_options",
                    buildJsonArray {
                        options.forEach { add(JsonPrimitive(it)) }
                    }
                )
                put("p_image_url", JsonNull)
                put("p_visibility", "public")
                put("p_expires_at", JsonNull)
            }
        )
    }

    suspend fun getComments(pollId: String): List<CommentDto> {
        val currentUserId = client.auth.currentUserOrNull()?.id

        return client.postgrest
            .rpc(
                function = "get_comments",
                parameters = buildJsonObject {
                    put("p_poll_id", pollId)
                    if (currentUserId == null) {
                        put("p_user_id", JsonNull)
                    } else {
                        put("p_user_id", currentUserId)
                    }
                }
            )
            .decodeList<CommentDto>()
    }

    suspend fun addComment(
        pollId: String,
        content: String
    ) {
        val currentUserId = client.auth.currentUserOrNull()?.id
            ?: error("Пользователь не авторизован")

        client.postgrest
            .rpc(
                function = "add_comment",
                parameters = buildJsonObject {
                    put("p_poll_id", pollId)
                    put("p_user_id", currentUserId)
                    put("p_content", content)
                }
            )
    }

    suspend fun getMyProfile(): ProfileDto {
        val currentUserId = client.auth.currentUserOrNull()?.id
            ?: error("Пользователь не авторизован")

        return client.postgrest
            .rpc(
                function = "get_user_profile",
                parameters = buildJsonObject {
                    put("p_profile_id", currentUserId)
                    put("p_current_user_id", currentUserId)
                }
            )
            .decodeAs<ProfileDto>()
    }

    suspend fun getMyPolls(): List<PollDto> {
        val currentUserId = client.auth.currentUserOrNull()?.id
            ?: error("Пользователь не авторизован")

        return client.postgrest
            .rpc(
                function = "get_user_polls",
                parameters = buildJsonObject {
                    put("p_profile_id", currentUserId)
                    put("p_current_user_id", currentUserId)
                    put("p_limit", 20)
                    put("p_offset", 0)
                }
            )
            .decodeList<PollDto>()
    }

    suspend fun deletePoll(pollId: String) {
        val userId = client.auth.currentUserOrNull()?.id
            ?: error("Не авторизован")

        client.postgrest.rpc(
            function = "delete_poll",
            parameters = buildJsonObject {
                put("p_poll_id", pollId)
                put("p_user_id", userId)
            }
        )
    }

    suspend fun updatePoll(pollId: String, question: String) {
        val userId = client.auth.currentUserOrNull()?.id
            ?: error("Не авторизован")

        client.postgrest.rpc(
            function = "update_poll",
            parameters = buildJsonObject {
                put("p_poll_id", pollId)
                put("p_user_id", userId)
                put("p_question", question)
            }
        )
    }

    suspend fun deleteComment(commentId: String) {
        val userId = client.auth.currentUserOrNull()?.id
            ?: error("Не авторизован")

        client.postgrest.rpc(
            function = "delete_comment",
            parameters = buildJsonObject {
                put("p_comment_id", commentId)
                put("p_user_id", userId)
            }
        )
    }

    suspend fun updateComment(commentId: String, text: String) {
        val userId = client.auth.currentUserOrNull()?.id
            ?: error("Не авторизован")

        client.postgrest.rpc(
            function = "update_comment",
            parameters = buildJsonObject {
                put("p_comment_id", commentId)
                put("p_user_id", userId)
                put("p_content", text)
            }
        )
    }
}

