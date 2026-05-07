package com.example.neakta.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val province: String
)