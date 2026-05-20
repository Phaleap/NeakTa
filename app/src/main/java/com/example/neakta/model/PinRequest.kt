package com.example.neakta.model

import java.math.BigDecimal

data class PinRequest(
    val provinceId: Int,
    val categoryId: Int,
    val title: String,
    val story: String,
    val address: String,
    val lat: BigDecimal,
    val lng: BigDecimal
)