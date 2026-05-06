package com.example.neakta.model

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val username: String,
    val displayName: String,
    val role: String
)