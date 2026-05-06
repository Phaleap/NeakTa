package com.example.neakta.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neakta.model.LoginRequest
import com.example.neakta.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String, val username: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class AuthViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = RetrofitClient.instance.login(
                    LoginRequest(email, password)
                )
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _loginState.value = LoginState.Success(
                        token = body.accessToken,
                        username = body.username
                    )
                } else {
                    _loginState.value = LoginState.Error("Invalid email or password")
                }
            } catch (_: Exception) {
                _loginState.value = LoginState.Error("Cannot connect to server")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}