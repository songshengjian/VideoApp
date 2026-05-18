package com.example.videoapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoapp.data.model.Video
import com.example.videoapp.data.repository.VideoRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    
    private val repository = VideoRepository()
    
    private val _videos = MutableLiveData<List<Video>>()
    val videos: LiveData<List<Video>> = _videos
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun loadHomeVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getHomeVideos()
                .onSuccess { videos ->
                    _videos.value = videos
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }
}
