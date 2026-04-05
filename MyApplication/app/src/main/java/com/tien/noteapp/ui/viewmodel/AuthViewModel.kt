package com.tien.noteapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tien.noteapp.data.repository.AuthRepository
import com.tien.noteapp.ui.state.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState = _registerState.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(authRepository.isUserAuthenticated())
    val isAuthenticated = _isAuthenticated.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            val result = authRepository.loginUser(email, password)
            _loginState.value = if (result.isSuccess) {
                _isAuthenticated.value = true
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            val result = authRepository.registerUser(email, password, displayName)
            _registerState.value = if (result.isSuccess) {
                _isAuthenticated.value = true
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logoutUser()
            _isAuthenticated.value = false
            _loginState.value = AuthState.Idle
            _registerState.value = AuthState.Idle
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email)
        }
    }

    fun clearLoginState() {
        _loginState.value = AuthState.Idle
    }

    fun clearRegisterState() {
        _registerState.value = AuthState.Idle
    }
}
