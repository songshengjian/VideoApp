package com.example.videoapp.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoapp.data.model.Video
import com.example.videoapp.data.model.VideoSearchResponse
import com.example.videoapp.data.repository.VideoRepository
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    
    private val repository = VideoRepository()
    
    private val _videos = MutableLiveData<List<Video>>()
    val videos: LiveData<List<Video>> = _videos
    
    private val _searchedChannels = MutableLiveData<List<String>>()
    val searchedChannels: LiveData<List<String>> = _searchedChannels
    
    private val _total = MutableLiveData<Int>()
    val total: LiveData<Int> = _total
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * 同步 Web 端多频道聚合搜索逻辑
     */
    fun searchVideos(keyword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.searchVideos(keyword = keyword)
                .onSuccess { response ->
                    _videos.value = response.list
                    _searchedChannels.value = response.searched_channels
                    _total.value = response.total
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }
}
