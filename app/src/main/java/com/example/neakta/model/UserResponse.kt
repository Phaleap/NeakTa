package com.example.neakta.model

data class UserResponse(
    val username: String,
    val displayName: String?,
    val email: String,
    val avatarUrl: String?,
    val karmaPoints: Int,
    val totalPins: Int,
    val provinceId: Int?,
    val createdAt: String?
)