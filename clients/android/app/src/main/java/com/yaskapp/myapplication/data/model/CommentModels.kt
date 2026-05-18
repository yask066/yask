package com.yaskapp.myapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentAuthorDto(
    val id: String,
    val username: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("avatar_url")
    val avatarUrl: String? = null
)

@Serializable
data class CommentDto(
    val id: String,
    val content: String,
    @SerialName("created_at")
    val createdAt: String,
    val author: CommentAuthorDto,

    @SerialName("is_mine")
    val isMine: Boolean = false,

    @SerialName("can_delete")
    val canDelete: Boolean = false
)
