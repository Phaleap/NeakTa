package com.example.neakta.network

import com.example.neakta.model.AuthResponse
import com.example.neakta.model.LoginRequest
import com.example.neakta.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<AuthResponse>
}