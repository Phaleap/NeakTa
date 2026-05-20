package com.example.neakta.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.neakta.data.SessionManager
import com.example.neakta.model.PinResponse
import com.example.neakta.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PinsState {
    object Loading : PinsState()
    data class Success(val pins: List<PinResponse>) : PinsState()
    data class Error(val message: String) : PinsState()
}

class PinViewModel(private val session: SessionManager) : ViewModel() {

    private val _pinsState = MutableStateFlow<PinsState>(PinsState.Loading)
    val pinsState: StateFlow<PinsState> = _pinsState

    init {
        fetchPins()
    }

    fun fetchPins() {
        viewModelScope.launch {
            _pinsState.value = PinsState.Loading
            try {
                val token = "Bearer ${session.getToken()}"
                val response = RetrofitClient.instance.getAllPins(token)
                if (response.isSuccessful) {
                    _pinsState.value = PinsState.Success(response.body() ?: emptyList())
                } else {
                    _pinsState.value = PinsState.Error("Failed to load pins")
                }
            } catch (e: Exception) {
                _pinsState.value = PinsState.Error(e.message ?: "Unknown error")
            }
        }
    }

    class Factory(private val session: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PinViewModel(session) as T
        }
    }
}