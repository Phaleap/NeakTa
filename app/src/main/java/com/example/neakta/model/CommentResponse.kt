package com.example.neakta.model

import java.util.UUID

data class CommentResponse(
    val id: String,
    val pinId: String,
    val userId: String,
    val username: String,
    val userAvatar: String?,
    val content: String,
    val stars: Int?,
    val createdAt: String
)