package com.example.neakta.ui.profile

import android.content.Context
import android.net.Uri
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: UserResponse, val pins: List<PinResponse>) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(private val session: SessionManager) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state

    private val _deletingPinId = MutableStateFlow<String?>(null)
    val deletingPinId: StateFlow<String?> = _deletingPinId

    private val _editingPin = MutableStateFlow<PinResponse?>(null)
    val editingPin: StateFlow<PinResponse?> = _editingPin

    private val _opError = MutableStateFlow<String?>(null)
    val opError: StateFlow<String?> = _opError

    // Track profile update status
    private val _isUpdatingProfile = MutableStateFlow(false)
    val isUpdatingProfile: StateFlow<Boolean> = _isUpdatingProfile

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

    fun updateProfile(displayName: String) {
        viewModelScope.launch {
            _isUpdatingProfile.value = true
            try {
                val token = "Bearer ${session.getToken()}"
                val body = mapOf("displayName" to displayName)
                val response = RetrofitClient.instance.updateProfile(token, body)
                if (response.isSuccessful) {
                    fetchProfile() // Refresh data
                } else {
                    _opError.value = "Update failed"
                }
            } catch (e: Exception) {
                _opError.value = e.message
            } finally {
                _isUpdatingProfile.value = false
            }
        }
    }

    fun uploadAvatar(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isUpdatingProfile.value = true
            try {
                val token = "Bearer ${session.getToken()}"
                
                // 1. Prepare file from Uri
                val file = uriToFile(context, uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // 2. Upload
                val response = RetrofitClient.instance.uploadAvatar(token, body)
                if (response.isSuccessful) {
                    fetchProfile()
                } else {
                    _opError.value = "Avatar upload failed"
                }
            } catch (e: Exception) {
                _opError.value = e.message
            } finally {
                _isUpdatingProfile.value = false
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_avatar_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return file
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
                            pins = current.pins.filter { it.id != pinId }
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
