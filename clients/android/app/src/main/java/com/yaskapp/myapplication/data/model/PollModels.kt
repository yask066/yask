package com.yaskapp.myapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorDto(
    val id: String,
    val username: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("avatar_url")
    val avatarUrl: String? = null
)

@Serializable
data class PollOptionDto(
    val id: String,
    val text: String,
    val position: Int,
    @SerialName("votes_count")
    val votesCount: Int,
    val percentage: Double
)

@Serializable
data class PollDto(
    val id: String,
    val question: String,

    @SerialName("image_url")
    val imageUrl: String? = null,

    val status: String = "active",
    val visibility: String = "public",

    @SerialName("expires_at")
    val expiresAt: String? = null,

    @SerialName("created_at")
    val createdAt: String,

    val author: AuthorDto,
    val options: List<PollOptionDto>,

    @SerialName("total_votes")
    val totalVotes: Int,

    @SerialName("likes_count")
    val likesCount: Int,

    @SerialName("comments_count")
    val commentsCount: Int,

    @SerialName("user_vote")
    val userVote: String? = null,

    @SerialName("liked_by_me")
    val likedByMe: Boolean,

    @SerialName("is_mine")
    val isMine: Boolean = false
)