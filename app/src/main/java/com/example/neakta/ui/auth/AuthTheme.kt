package com.example.neakta.ui.auth

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.neakta.R

// ─── Shared Font Families ───────────────────────────────────
val CormorantGaramond = FontFamily(
    Font(R.font.cormorant_garamond_light,    FontWeight.Light),
    Font(R.font.cormorant_garamond_regular,  FontWeight.Normal),
    Font(R.font.cormorant_garamond_italic,   FontWeight.Normal, FontStyle.Italic),
    Font(R.font.cormorant_garamond_semibold, FontWeight.SemiBold),
)

val Cinzel = FontFamily(
    Font(R.font.cinzel_regular,  FontWeight.Normal),
    Font(R.font.cinzel_semibold, FontWeight.SemiBold),
)

// ─── Shared Shape ───────────────────────────────────────────
val CardShape = RoundedCornerShape(
    topStart    = 28.dp,
    topEnd      = 28.dp,
    bottomStart = 0.dp,
    bottomEnd   = 0.dp
)