package com.example.neakta.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.neakta.data.SessionManager
import com.example.neakta.model.CommentRequest
import com.example.neakta.model.CommentResponse
import com.example.neakta.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _comments = MutableStateFlow<List<CommentResponse>>(emptyList())
    val comments: StateFlow<List<CommentResponse>> = _comments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending

    private var currentPinId: String = ""

    fun loadComments(pinId: String) {
        currentPinId = pinId
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = "Bearer ${sessionManager.getToken()}"
                val response = RetrofitClient.instance.getComments(token, pinId)
                if (response.isSuccessful) {
                    _comments.value = response.body() ?: emptyList()
                    android.util.Log.d("COMMENT_DEBUG", "Comments loaded: ${_comments.value.map { "content='${it.content}' stars=${it.stars}" }}")
                }
            } catch (e: Exception) {
                android.util.Log.e("COMMENT_DEBUG", "Load error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun postComment(pinId: String, content: String, stars: Int?, onDone: () -> Unit) {
        // Allow post if there's content OR stars (not requiring both)
        if (content.isBlank() && stars == null) return

        viewModelScope.launch {
            _isSending.value = true
            try {
                val token = "Bearer ${sessionManager.getToken()}"
                val response = RetrofitClient.instance.addComment(
                    token,
                    pinId,
                    body = CommentRequest(content = content, stars = stars)
                )
                if (response.isSuccessful) {
                    loadComments(pinId)
                    onDone()
                }
            } catch (e: Exception) {
                android.util.Log.e("COMMENT_DEBUG", "Post error: ${e.message}")
            } finally {
                _isSending.value = false
            }
        }
    }

    fun editComment(commentId: String, content: String, stars: Int?, onDone: () -> Unit) {
        if (content.isBlank() && stars == null) return

        viewModelScope.launch {
            _isSending.value = true
            try {
                val token = "Bearer ${sessionManager.getToken()}"
                val response = RetrofitClient.instance.editComment(
                    token,
                    commentId,
                    body = CommentRequest(content = content, stars = stars)
                )
                if (response.isSuccessful) {
                    loadComments(currentPinId)
                    onDone()
                }
            } catch (e: Exception) {
                android.util.Log.e("COMMENT_DEBUG", "Edit error: ${e.message}")
            } finally {
                _isSending.value = false
            }
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${sessionManager.getToken()}"
                val response = RetrofitClient.instance.deleteComment(token, commentId)
                if (response.isSuccessful) {
                    // Optimistically remove from list immediately
                    _comments.value = _comments.value.filter { it.id != commentId }

                }
            } catch (e: Exception) {
                android.util.Log.e("COMMENT_DEBUG", "Delete error: ${e.message}")
            }
        }
    }

    class Factory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return CommentViewModel(sessionManager) as T
        }
    }
}