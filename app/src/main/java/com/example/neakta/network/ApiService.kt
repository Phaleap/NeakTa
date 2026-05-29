package com.example.neakta.network

import com.example.neakta.model.AuthResponse
import com.example.neakta.model.CategoryResponse
import com.example.neakta.model.CommentRequest
import com.example.neakta.model.CommentResponse
import com.example.neakta.model.LoginRequest
import com.example.neakta.model.PinRequest
import com.example.neakta.model.PinResponse
import com.example.neakta.model.ProvinceResponse
import com.example.neakta.model.ProvinceStatsResponse
import com.example.neakta.model.RegisterRequest
import com.example.neakta.model.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @GET("api/pins")
    suspend fun getAllPins(
        @Header("Authorization") token: String
    ): Response<List<PinResponse>>

    @GET("api/pins/{id}")
    suspend fun getPin(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<PinResponse>

    @GET("api/pins/province/{provinceId}")
    suspend fun getPinsByProvince(
        @Header("Authorization") token: String,
        @Path("provinceId") provinceId: Int
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

    @PUT("api/users/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<UserResponse>

    @Multipart
    @PUT("api/users/me/avatar")    // ✅ matches your Spring controller
    suspend fun uploadAvatar(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<Map<String, String>>

    @GET("api/provinces")
    suspend fun getProvinces(
        @Header("Authorization") token: String
    ): Response<List<ProvinceResponse>>

    @GET("api/categories")
    suspend fun getCategories(
        @Header("Authorization") token: String
    ): Response<List<CategoryResponse>>

    @GET("api/stats/leaderboard")
    suspend fun getProvinceLeaderboard(
        @Header("Authorization") token: String
    ): Response<List<ProvinceStatsResponse>>

    @GET("api/saved")
    suspend fun getSavedPins(
        @Header("Authorization") token: String
    ): Response<List<PinResponse>>

    @POST("api/saved/{pinId}")
    suspend fun toggleSavedPin(
        @Header("Authorization") token: String,
        @Path("pinId") pinId: String
    ): Response<Map<String, String>>

    @Multipart
    @POST("api/pins/{id}/photos")
    suspend fun uploadPinPhoto(
        @Header("Authorization") token: String,
        @Path("id") pinId: String,
        @Part file: MultipartBody.Part
    ): Response<Map<String, String>>

    @Multipart
    @PUT("api/pins/{id}/photos")
    suspend fun updatePinPhoto(
        @Header("Authorization") token: String,
        @Path("id") pinId: String,
        @Part file: MultipartBody.Part
    ): Response<Map<String, String>>

    @Multipart
    @POST("api/pins/{id}/photo")
    suspend fun uploadPinPhotoSingular(
        @Header("Authorization") token: String,
        @Path("id") pinId: String,
        @Part file: MultipartBody.Part
    ): Response<Map<String, String>>

    @Multipart
    @PUT("api/pins/{id}/photo")
    suspend fun updatePinPhotoSingular(
        @Header("Authorization") token: String,
        @Path("id") pinId: String,
        @Part file: MultipartBody.Part
    ): Response<Map<String, String>>

    @POST("api/votes/{pinId}")
    suspend fun vote(
        @Header("Authorization") token: String,
        @Path("pinId") pinId: String,
        @Query("type") type: String
    ): Response<Map<String, String>>

    @DELETE("api/pins/{id}")
    suspend fun deletePin(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    @PUT("api/pins/{id}")
    suspend fun updatePin(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: PinRequest
    ): Response<PinResponse>

    @GET("api/pins/nearby")
    suspend fun getNearbyPins(
        @Header("Authorization") token: String,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radiusKm") radiusKm: Double = 10.0
    ): Response<List<PinResponse>>

    @GET("api/pins/{pinId}/comments")
    suspend fun getComments(
        @Header("Authorization") token: String,
        @Path("pinId") pinId: String
    ): Response<List<CommentResponse>>

    @POST("api/pins/{pinId}/comments")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Path("pinId") pinId: String,
        @Body body: CommentRequest  // 👈 change from Map<String, String>
    ): Response<CommentResponse>

    @PUT("comments/{commentId}")
    suspend fun editComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: String,
        @Body body: CommentRequest
    ): Response<CommentResponse>

    @DELETE("comments/{commentId}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: String
    ): Response<Unit>

}
