package com.example.expencetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expencetracker.data.AuthManager
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.fold

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        checkAuthStatus()
    }

    fun isUserLoggedIn(): Boolean {
        return authManager.isUserLoggedIn()
    }

    private fun checkAuthStatus() {
        _currentUser.value = authManager.currentUser
        if (authManager.isUserLoggedIn()) {
            _authState.value = AuthState.Authenticated
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authManager.signUp(email, password, name)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Signup failed")
                }
            )
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authManager.signIn(email, password)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Login failed")
                }
            )
        }
    }

    fun signInWithGoogle(account: com.google.android.gms.auth.api.signin.GoogleSignInAccount) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authManager.signInWithGoogle(account)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Google Sign-In failed")
                }
            )
        }
    }

    fun signOut() {
        authManager.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun updateProfile(name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authManager.updateProfile(name)
            result.fold(
                onSuccess = {
                    _currentUser.value = authManager.currentUser
                    _authState.value = AuthState.ProfileUpdated
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Update failed")
                }
            )
        }
    }

    fun updateEmail(newEmail: String, currentPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authManager.updateEmail(newEmail, currentPassword)
            result.fold(
                onSuccess = {
                    _currentUser.value = authManager.currentUser
                    _authState.value = AuthState.EmailUpdated
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Email update failed")
                }
            )
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authManager.updatePassword(currentPassword, newPassword)
            result.fold(
                onSuccess = {
                    _authState.value = AuthState.PasswordUpdated
                },
                onFailure = { exception ->
                    _authState.value =
                        AuthState.Error(exception.message ?: "Password update failed")
                }
            )
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authManager.resetPassword(email)
            result.fold(
                onSuccess = {
                    _authState.value = AuthState.PasswordResetSent
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Reset failed")
                }
            )
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object ProfileUpdated : AuthState()
    object EmailUpdated : AuthState()
    object PasswordUpdated : AuthState()
    object PasswordResetSent : AuthState()
    data class Error(val message: String) : AuthState()
}
