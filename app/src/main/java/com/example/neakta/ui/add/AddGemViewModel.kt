package com.example.neakta.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
                        val part = uriToMultipart(context, uri)
                        if (part != null) {
                            RetrofitClient.instance.uploadPinPhoto(token, createdPin.id, part)
                        }
                    } catch (e: Exception) {
                        // Don't fail the whole submission if a photo upload fails
                        android.util.Log.e("AddGemVM", "Photo upload failed: ${e.message}")
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
        uri: android.net.Uri
    ): MultipartBody.Part? {
        return try {
            val stream = context.contentResolver.openInputStream(uri) ?: return null
            val bytes = stream.readBytes()
            stream.close()
            val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", "photo_${System.currentTimeMillis()}.jpg", requestBody)
        } catch (e: Exception) {
            null
        }
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
