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
    
    private val _sections = MutableLiveData<List<HomeSection>>()
    val sections: LiveData<List<HomeSection>> = _sections
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun loadHomeVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val sectionList = mutableListOf<HomeSection>()
            
            // 定义分类：typeId 和显示名称
            val categories = listOf(
                Pair(1, "热播电影"),
                Pair(2, "热播连续剧"),
                Pair(3, "热播综艺"),
                Pair(4, "热播动漫"),
                Pair(5, "热播短剧")
            )
            
            var hasError = false
            var errorMessage = ""
            
            categories.forEach { (typeId, name) ->
                try {
                    val result = repository.getCategoryVideos(typeId = typeId, page = 1)
                    result.onSuccess { videos ->
                        if (videos.isNotEmpty()) {
                            // 每个分类取最新6个
                            sectionList.add(HomeSection(name, typeId, videos.take(6)))
                        }
                    }.onFailure { exception ->
                        // 单个分类失败不影响其他分类
                    }
                } catch (e: Exception) {
                    // 忽略单个分类错误
                }
            }
            
            if (sectionList.isEmpty()) {
                _error.value = "加载首页数据失败"
            } else {
                _sections.value = sectionList
            }
            
            _isLoading.value = false
        }
    }
}
