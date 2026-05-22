package com.example.neakta.model

import java.math.BigDecimal

data class ProvinceResponse(
    val id: Int,
    val nameEn: String,
    val nameKm: String,
    val region: String?,
    val centerLat: BigDecimal?,
    val centerLng: BigDecimal?
)
