package com.example.neakta.model

data class ProvinceStatsResponse(
    val id: String,
    val province: ProvinceResponse,
    val totalPins: Int,
    val totalUpvotes: Int,
    val totalContributors: Int,
    val rank: Int?,
    val lastUpdated: String?
)