package com.example.neakta.network

import com.example.neakta.model.AuthResponse
import com.example.neakta.model.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>  // ← must be Response<AuthResponse> not just AuthResponse
}