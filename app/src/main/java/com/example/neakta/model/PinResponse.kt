package com.example.neakta.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class PinResponse(
    val id: String,
    val title: String,
    val story: String,
    val address: String?,
    val lat: BigDecimal?,
    val lng: BigDecimal?,
    val score: Int,
    val upvoteCount: Int,
    val status: String?,
    val isVerified: Boolean,
    val createdAt: String?,
    val authorUsername: String?,
    val provinceName: String?,
    val categoryName: String?,
    val categoryIcon: String?,
    val categoryColor: String?,
    @SerializedName(value = "imageUrl", alternate = ["image_url", "url"])
    val imageUrl: String?,
    val tags: List<String>? = null,
    @SerializedName(value = "mediaUrls", alternate = ["media_urls", "photos", "photoUrls"])
    val mediaUrls: List<String>? = null,
    @SerializedName(value = "localDirections", alternate = ["local_directions"])
    val localDirections: String? = null,
    @SerializedName(value = "stillExistsPct", alternate = ["still_exists_pct"])
    val stillExistsPct: BigDecimal? = null
)
