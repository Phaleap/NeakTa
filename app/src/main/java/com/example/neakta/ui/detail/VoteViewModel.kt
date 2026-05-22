package com.example.neakta.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.neakta.data.SessionManager
import com.example.neakta.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VoteViewModel(private val session: SessionManager) : ViewModel() {

    private val _voteCount = MutableStateFlow(0)
    val voteCount: StateFlow<Int> = _voteCount

    private val _hasVoted = MutableStateFlow(false)
    val hasVoted: StateFlow<Boolean> = _hasVoted

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    fun init(initialVotes: Int) {
        _voteCount.value = initialVotes
    }

    fun toggleVote(pinId: String) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${session.getToken()}"
                val response = RetrofitClient.instance.vote(token, pinId, "UPVOTE")
                if (response.isSuccessful) {
                    val message = response.body()?.get("message") ?: ""
                    if (message == "Vote removed") {
                        _hasVoted.value = false
                        _voteCount.value -= 1
                    } else {
                        _hasVoted.value = true
                        _voteCount.value += 1
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("VoteVM", "Vote failed: ${e.message}")
            }
        }
    }

    fun flagPin(pinId: String) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${session.getToken()}"
                RetrofitClient.instance.vote(token, pinId, "FLAG")
            } catch (e: Exception) {
                android.util.Log.e("VoteVM", "Flag failed: ${e.message}")
            }
        }
    }

    fun confirmExists(pinId: String) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${session.getToken()}"
                RetrofitClient.instance.vote(token, pinId, "CONFIRM_EXISTS")
            } catch (e: Exception) {
                android.util.Log.e("VoteVM", "Confirm exists failed: ${e.message}")
            }
        }
    }

    fun toggleSaved(pinId: String) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${session.getToken()}"
                val response = RetrofitClient.instance.toggleSavedPin(token, pinId)
                if (response.isSuccessful) {
                    _isSaved.value = response.body()?.get("message") == "Pin saved"
                }
            } catch (e: Exception) {
                android.util.Log.e("VoteVM", "Save failed: ${e.message}")
            }
        }
    }

    class Factory(private val session: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return VoteViewModel(session) as T
        }
    }
}
