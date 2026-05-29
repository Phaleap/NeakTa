package com.example.neakta.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import android.webkit.MimeTypeMap
import com.example.neakta.model.CategoryResponse
import com.example.neakta.data.SessionManager
import com.example.neakta.model.PinRequest
import com.example.neakta.model.ProvinceResponse
import com.example.neakta.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.math.BigDecimal

sealed class AddGemState {
    object Idle : AddGemState()
    object Loading : AddGemState()
    object Success : AddGemState()
    data class Error(val message: String) : AddGemState()
}

class AddGemViewModel(private val session: SessionManager) : ViewModel() {

    private val _state = MutableStateFlow<AddGemState>(AddGemState.Idle)
    val state: StateFlow<AddGemState> = _state

    private val _provinces = MutableStateFlow<List<ProvinceResponse>>(emptyList())
    val provinces: StateFlow<List<ProvinceResponse>> = _provinces

    private val _categories = MutableStateFlow<List<CategoryResponse>>(emptyList())
    val categories: StateFlow<List<CategoryResponse>> = _categories

    init {
        fetchCatalogs()
    }

    private fun fetchCatalogs() {
        viewModelScope.launch {
            val token = "Bearer ${session.getToken()}"
            try {
                val provinceResponse = RetrofitClient.instance.getProvinces(token)
                if (provinceResponse.isSuccessful) {
                    _provinces.value = provinceResponse.body().orEmpty()
                }

                val categoryResponse = RetrofitClient.instance.getCategories(token)
                if (categoryResponse.isSuccessful) {
                    _categories.value = categoryResponse.body().orEmpty()
                }
            } catch (e: Exception) {
                android.util.Log.e("AddGemVM", "Catalog load failed: ${e.message}")
            }
        }
    }

    fun submitPin(
        title: String,
        story: String,
        address: String,
        provinceName: String,
        categoryName: String,
        lat: Double,
        lng: Double,
        photoUris: List<android.net.Uri>,
        context: android.content.Context,
        localDirections: String = "",       // ADD THIS
        tags: List<String> = emptyList()    // ADD THIS
    ) {
        viewModelScope.launch {
            _state.value = AddGemState.Loading
            try {
                val provinceId = resolveProvinceId(provinceName)
                val categoryId = resolveCategoryId(categoryName)

                if (provinceId == null || categoryId == null) {
                    _state.value = AddGemState.Error("Invalid province or category")
                    return@launch
                }

                val request = PinRequest(
                    provinceId = provinceId,
                    categoryId = categoryId,
                    title = title,
                    story = story,
                    address = address,
                    lat = BigDecimal.valueOf(lat),
                    lng = BigDecimal.valueOf(lng),
                    localDirections = localDirections,   // ADD
                    tags            = tags                      // ADD — needs to be a parameter
                )

                val token = "Bearer ${session.getToken()}"

                // Step 1: create the pin
                val response = RetrofitClient.instance.createPin(token, request)
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _state.value = AddGemState.Error("Failed: $errorBody")
                    return@launch
                }

                val createdPin = response.body()!!

                // Step 2: upload photos if any
                for (uri in photoUris) {
                    try {
                        val uploadResponse = uploadPhotoWithFallbackFieldNames(
                            token = token,
                            pinId = createdPin.id,
                            context = context,
                            uri = uri
                        )

                        if (uploadResponse == null) {
                            _state.value = AddGemState.Error("Could not read selected photo")
                            return@launch
                        }

                        if (!uploadResponse.isSuccessful) {
                            val detail = uploadResponse.errorBody()?.string()?.takeIf { it.isNotBlank() }
                                ?: uploadResponse.message().takeIf { it.isNotBlank() }
                                ?: "No error body"
                            _state.value = AddGemState.Error("Photo upload failed: HTTP ${uploadResponse.code()} - $detail")
                            return@launch
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AddGemVM", "Photo upload failed: ${e.message}")
                        _state.value = AddGemState.Error("Photo upload failed: ${e.message ?: "Unknown error"}")
                        return@launch
                    }
                }

                _state.value = AddGemState.Success

            } catch (e: Exception) {
                _state.value = AddGemState.Error(e.message ?: "Network error")
            }
        }
    }

    private fun uriToMultipart(
        context: android.content.Context,
        uri: android.net.Uri,
        fieldName: String
    ): MultipartBody.Part? {
        return try {
            val stream = context.contentResolver.openInputStream(uri) ?: return null
            val bytes = stream.readBytes()
            stream.close()
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(mimeType)
                ?.takeIf { it.isNotBlank() }
                ?: "jpg"
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(
                fieldName,
                "photo_${System.currentTimeMillis()}.$extension",
                requestBody
            )
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun uploadPhotoWithFallbackFieldNames(
        token: String,
        pinId: String,
        context: android.content.Context,
        uri: android.net.Uri
    ): Response<Map<String, String>>? {
        var lastResponse: Response<Map<String, String>>? = null
        val uploadRoutes: List<suspend (MultipartBody.Part) -> Response<Map<String, String>>> =
            listOf(
                { part -> RetrofitClient.instance.uploadPinPhoto(token, pinId, part) },
                { part -> RetrofitClient.instance.updatePinPhoto(token, pinId, part) },
                { part -> RetrofitClient.instance.uploadPinPhotoSingular(token, pinId, part) },
                { part -> RetrofitClient.instance.updatePinPhotoSingular(token, pinId, part) }
            )

        for (upload in uploadRoutes) {
            for (fieldName in listOf("file", "photo", "image")) {
                val part = uriToMultipart(context, uri, fieldName) ?: return null
                val response = upload(part)
                if (response.isSuccessful) return response
                lastResponse = response
            }
        }
        return lastResponse
    }

    fun resetState() {
        _state.value = AddGemState.Idle
    }

    private fun resolveProvinceId(name: String): Int? {
        _provinces.value.firstOrNull {
            it.nameEn.equals(name, ignoreCase = true) || it.nameKm == name
        }?.let { return it.id }

        return cambodianProvinces.indexOfFirst { it.equals(name, ignoreCase = true) }
            .takeIf { it >= 0 }
            ?.plus(1)
    }

    private fun resolveCategoryId(name: String): Int? {
        _categories.value.firstOrNull {
            it.nameEn.equals(name, ignoreCase = true) || it.nameKh == name
        }?.let { return it.id }

        return gemCategories.indexOfFirst { it.equals(name, ignoreCase = true) }
            .takeIf { it >= 0 }
            ?.plus(1)
    }

    class Factory(private val session: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AddGemViewModel(session) as T
        }
    }
}
