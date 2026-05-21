package com.example.neakta.model

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
    val imageUrl: String?
)