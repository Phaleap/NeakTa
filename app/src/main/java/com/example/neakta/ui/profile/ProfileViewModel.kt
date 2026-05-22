package com.example.neakta.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.neakta.data.SessionManager
import com.example.neakta.model.PinRequest
import com.example.neakta.model.PinResponse
import com.example.neakta.model.UserResponse
import com.example.neakta.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: UserResponse, val pins: List<PinResponse>) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(private val session: SessionManager) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state

    // ID of the pin currently being deleted (shows loading on that row)
    private val _deletingPinId = MutableStateFlow<String?>(null)
    val deletingPinId: StateFlow<String?> = _deletingPinId

    // Non-null while a pin is being edited (drives bottom sheet open)
    private val _editingPin = MutableStateFlow<PinResponse?>(null)
    val editingPin: StateFlow<PinResponse?> = _editingPin

    // Transient one-shot error for delete/edit ops (snackbar)
    private val _opError = MutableStateFlow<String?>(null)
    val opError: StateFlow<String?> = _opError

    init { fetchProfile() }

    fun fetchProfile() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            try {
                val token = "Bearer ${session.getToken()}"

                val userResponse = RetrofitClient.instance.getMe(token)
                if (!userResponse.isSuccessful) {
                    _state.value = ProfileState.Error("Failed to load profile")
                    return@launch
                }
                val user = userResponse.body()!!

                val pinsResponse = RetrofitClient.instance.getAllPins(token)
                val myPins = if (pinsResponse.isSuccessful) {
                    pinsResponse.body()
                        ?.filter { it.authorUsername == user.username }
                        ?: emptyList()
                } else emptyList()

                _state.value = ProfileState.Success(user, myPins)
            } catch (e: Exception) {
                _state.value = ProfileState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deletePin(pinId: String) {
        viewModelScope.launch {
            _deletingPinId.value = pinId
            try {
                val token = "Bearer ${session.getToken()}"
                val response = RetrofitClient.instance.deletePin(token, pinId)
                if (response.isSuccessful) {
                    val current = _state.value
                    if (current is ProfileState.Success) {
                        _state.value = current.copy(
                            pins = current.pins.filter { it.id != pinId }  // String != String ✅
                        )
                    }
                } else {
                    _opError.value = "Delete failed (${response.code()})"
                }
            } catch (e: Exception) {
                _opError.value = e.message ?: "Unknown error"
            } finally {
                _deletingPinId.value = null
            }
        }
    }

    fun startEditing(pin: PinResponse) {
        _editingPin.value = pin
    }

    fun cancelEditing() {
        _editingPin.value = null
    }

    fun saveEdit(pinId: String, newTitle: String, newStory: String) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${session.getToken()}"
                val currentPin = _editingPin.value
                    ?: throw IllegalStateException("No pin selected for editing")

                val provinces = RetrofitClient.instance.getProvinces(token).body().orEmpty()
                val categories = RetrofitClient.instance.getCategories(token).body().orEmpty()

                val provinceId = provinces.firstOrNull {
                    it.nameEn.equals(currentPin.provinceName, ignoreCase = true) ||
                        it.nameKm == currentPin.provinceName
                }?.id ?: throw IllegalStateException("Province not found")

                val categoryId = categories.firstOrNull {
                    it.nameEn.equals(currentPin.categoryName, ignoreCase = true) ||
                        it.nameKh == currentPin.categoryName
                }?.id ?: throw IllegalStateException("Category not found")

                val body = PinRequest(
                    provinceId = provinceId,
                    categoryId = categoryId,
                    title = newTitle.trim(),
                    story = newStory.trim(),
                    address = currentPin.address ?: currentPin.provinceName.orEmpty(),
                    lat = currentPin.lat ?: BigDecimal.valueOf(11.5564),
                    lng = currentPin.lng ?: BigDecimal.valueOf(104.9282)
                )
                val response = RetrofitClient.instance.updatePin(token, pinId, body)
                if (response.isSuccessful) {
                    val updated = response.body()!!
                    val current = _state.value
                    if (current is ProfileState.Success) {
                        _state.value = current.copy(
                            pins = current.pins.map { if (it.id == updated.id) updated else it }
                        )
                    }
                    _editingPin.value = null
                } else {
                    _opError.value = "Update failed (${response.code()})"
                }
            } catch (e: Exception) {
                _opError.value = e.message ?: "Unknown error"
            }
        }
    }

    fun clearOpError() { _opError.value = null }

    class Factory(private val session: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(session) as T
        }
    }
}
