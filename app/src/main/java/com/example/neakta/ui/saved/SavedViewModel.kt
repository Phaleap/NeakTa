package com.example.neakta.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.neakta.data.SessionManager
import com.example.neakta.network.RetrofitClient
import com.example.neakta.ui.home.PinCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SavedState {
    object Loading : SavedState()
    data class Success(val pins: List<PinCard>) : SavedState()
    data class Error(val message: String) : SavedState()
}

class SavedViewModel(private val session: SessionManager) : ViewModel() {

    private val _state = MutableStateFlow<SavedState>(SavedState.Loading)
    val state: StateFlow<SavedState> = _state

    init { fetchSavedPins() }

    fun fetchSavedPins() {
        viewModelScope.launch {
            _state.value = SavedState.Loading
            try {
                val token = "Bearer ${session.getToken()}"
                val response = RetrofitClient.instance.getSavedPins(token)
                val pins = response.body() ?: emptyList()
                _state.value = SavedState.Success(pins.map { pin ->
                    PinCard(
                        id              = pin.id,
                        title           = pin.title,
                        province        = pin.provinceName ?: "",
                        category        = pin.categoryName ?: "",
                        votes           = pin.upvoteCount,
                        story           = pin.story,
                        imageUrl        = pin.imageUrl ?: "",
                        author          = pin.authorUsername ?: "",
                        timeAgo         = pin.createdAt?.take(10) ?: "",
                        lat             = pin.lat?.toDouble() ?: 11.5564,
                        lng             = pin.lng?.toDouble() ?: 104.9282,
                        tags            = pin.tags ?: emptyList(),
                        mediaUrls       = pin.mediaUrls ?: emptyList(),
                        localDirections = pin.localDirections ?: "",
                        stillExistsPct  = pin.score.coerceIn(0, 100).takeIf { it > 0 } ?: 97,
                        yearDiscovered  = pin.createdAt?.take(4) ?: "2024"
                    )
                })
            } catch (e: Exception) {
                _state.value = SavedState.Error(e.message ?: "Failed to load saved pins")
            }
        }
    }

    class Factory(private val session: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SavedViewModel(session) as T
        }
    }
}