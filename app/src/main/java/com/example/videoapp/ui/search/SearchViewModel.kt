package com.example.videoapp.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoapp.data.model.Video
import com.example.videoapp.data.repository.VideoRepository
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    
    private val repository = VideoRepository()
    
    private val _videos = MutableLiveData<List<Video>>()
    val videos: LiveData<List<Video>> = _videos
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun searchVideos(keyword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            repository.searchVideos(keyword = keyword)
                .onSuccess { videos ->
                    _videos.value = videos
                }
                .onFailure { exception ->
                    // 处理错误
                }
            
            _isLoading.value = false
        }
    }
}
