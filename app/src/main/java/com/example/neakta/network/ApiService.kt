package com.example.neakta.network

import com.example.neakta.model.AuthResponse
import com.example.neakta.model.LoginRequest
import com.example.neakta.model.PinRequest
import com.example.neakta.model.PinResponse
import com.example.neakta.model.RegisterRequest
import com.example.neakta.model.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @GET("api/pins")
    suspend fun getAllPins(
        @Header("Authorization") token: String
    ): Response<List<PinResponse>>

    @POST("api/pins")
    suspend fun createPin(
        @Header("Authorization") token: String,
        @Body request: PinRequest
    ): Response<PinResponse>

    @GET("api/users/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): Response<UserResponse>

    @Multipart
    @POST("api/pins/{id}/photos")
    suspend fun uploadPinPhoto(
        @Header("Authorization") token: String,
        @Path("id") pinId: String,
        @Part file: MultipartBody.Part
    ): Response<Map<String, String>>
}