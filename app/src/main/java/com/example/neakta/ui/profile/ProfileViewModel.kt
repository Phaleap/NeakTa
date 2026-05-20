package com.example.neakta.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.neakta.data.SessionManager
import com.example.neakta.model.UserResponse
import com.example.neakta.network.RetrofitClient
import com.example.neakta.model.PinResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: UserResponse, val pins: List<PinResponse>) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(private val session: SessionManager) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state

    init {
        fetchProfile()
    }

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

                // Fetch all pins and filter by current user
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

    class Factory(private val session: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(session) as T
        }
    }
}