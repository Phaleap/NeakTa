package com.example.neakta.ui.core

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val displayName: String) {
    ENGLISH("English"),
    KHMER("ភាសាខ្មែរ")
}

class LanguageState(initial: AppLanguage = AppLanguage.ENGLISH) {
    var current by mutableStateOf(initial)
}

val LocalAppLanguage = compositionLocalOf { LanguageState() }