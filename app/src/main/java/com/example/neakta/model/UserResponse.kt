package com.example.neakta.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val username: String,
    val displayName: String?,
    val email: String,
    @SerializedName(value = "avatarUrl", alternate = ["avatar_url"])
    val avatarUrl: String?,
    val karmaPoints: Int,
    val totalPins: Int,
    val provinceId: Int?,
    val createdAt: String?
)
