package com.example.neakta.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.neakta.data.SessionManager
import com.example.neakta.model.PinRequest
import com.example.neakta.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    fun submitPin(
        title: String,
        story: String,
        address: String,
        provinceName: String,
        categoryName: String,
        lat: Double,
        lng: Double
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
                    lng = BigDecimal.valueOf(lng)
                )

                val token = "Bearer ${session.getToken()}"
                val response = RetrofitClient.instance.createPin(token, request)

                if (response.isSuccessful) {
                    _state.value = AddGemState.Success
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _state.value = AddGemState.Error("Failed: $errorBody")
                }
            } catch (e: Exception) {
                _state.value = AddGemState.Error(e.message ?: "Network error")
            }
        }
    }

    fun resetState() {
        _state.value = AddGemState.Idle
    }

    // TODO: Replace with real /api/provinces endpoint when available.
    // These IDs assume DB is seeded in alphabetical order (1-indexed).
    private fun resolveProvinceId(name: String): Int? {
        return cambodianProvinces.indexOfFirst {
            it.equals(name, ignoreCase = true)
        }.takeIf { it >= 0 }?.plus(1)
    }

    // TODO: Replace with real /api/categories endpoint when available.
    private fun resolveCategoryId(name: String): Int? {
        return gemCategories.indexOfFirst {
            it.equals(name, ignoreCase = true)
        }.takeIf { it >= 0 }?.plus(1)
    }

    class Factory(private val session: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AddGemViewModel(session) as T
        }
    }
}