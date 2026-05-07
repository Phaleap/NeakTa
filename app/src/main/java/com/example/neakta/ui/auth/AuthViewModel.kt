package com.example.neakta.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.neakta.data.SessionManager
import com.example.neakta.model.LoginRequest
import com.example.neakta.model.RegisterRequest
import com.example.neakta.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ─── States ──────────────────────────────────────────────────
sealed class LoginState {
    object Idle    : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    object Idle    : RegisterState()
    object Loading : RegisterState()
    data class Success(val token: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

// ─── ViewModel ───────────────────────────────────────────────
class AuthViewModel(private val session: SessionManager) : ViewModel() {

    // ── Login ─────────────────────────────────────────────────
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
                val response = RetrofitClient.instance.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    session.saveToken(token)                  // ← persist token
                    _loginState.value = LoginState.Success(token)
                } else {
                    _loginState.value = LoginState.Error("Invalid email or password")
                }
            } catch (_: Exception) {
                _loginState.value = LoginState.Error("Cannot connect to server")
            }
        }
    }

    // ── Register ──────────────────────────────────────────────
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _registerState.value = RegisterState.Error("All fields are required")
            return
        }
        if (password != confirmPassword) {
            _registerState.value = RegisterState.Error("Passwords do not match")
            return
        }
        if (password.length < 6) {
            _registerState.value = RegisterState.Error("Password must be at least 6 characters")
            return
        }
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val response = RetrofitClient.instance.register(
                    RegisterRequest(username = username, email = email, password = password)
                )
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    session.saveToken(token)                  // ← persist token
                    _registerState.value = RegisterState.Success(token)
                } else {
                    val errorMsg = when (response.code()) {
                        409  -> "Email or username already taken"
                        else -> "Registration failed. Try again."
                    }
                    _registerState.value = RegisterState.Error(errorMsg)
                }
            } catch (_: Exception) {
                _registerState.value = RegisterState.Error("Cannot connect to server")
            }
        }
    }

    // ── Reset ─────────────────────────────────────────────────
    fun resetState() {
        _loginState.value    = LoginState.Idle
        _registerState.value = RegisterState.Idle
    }

    // ── Factory ───────────────────────────────────────────────
    class Factory(private val session: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(session) as T
        }
    }
}