package com.example.neakta.model

data class AuthResponse(
    val token: String   // matches Map.of("token", token) from backend
)