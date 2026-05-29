package com.example.neakta.network

fun normalizeImageUrl(url: String?): String {
    val trimmed = url?.trim().orEmpty()
    if (trimmed.isBlank()) return ""

    return when {
        trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.startsWith("content://", ignoreCase = true) ||
            trimmed.startsWith("file://", ignoreCase = true) -> trimmed

        trimmed.startsWith("/") -> RetrofitClient.BASE_URL.trimEnd('/') + trimmed

        else -> RetrofitClient.BASE_URL + trimmed
    }
}

fun normalizeImageUrls(urls: List<String>?): List<String> =
    urls.orEmpty()
        .map(::normalizeImageUrl)
        .filter { it.isNotBlank() }
