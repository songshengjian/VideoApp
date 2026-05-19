package com.example.videoapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoapp.data.repository.VideoRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    
    private val repository = VideoRepository()
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult
    
    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> = _registerResult
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.login(username, password)
                .onSuccess { user ->
                    _loginResult.value = true
                }
                .onFailure { exception ->
                    _error.value = "登录失败：${exception.message}"
                    _loginResult.value = false
                }
            
            _isLoading.value = false
        }
    }
    
    fun register(username: String, password: String, email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.register(username, password, email)
                .onSuccess { user ->
                    _registerResult.value = true
                }
                .onFailure { exception ->
                    _error.value = "注册失败：${exception.message}"
                    _registerResult.value = false
                }
            
            _isLoading.value = false
        }
    }
}
