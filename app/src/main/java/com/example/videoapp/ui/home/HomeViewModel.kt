package com.example.videoapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoapp.data.model.Video
import com.example.videoapp.data.repository.VideoRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    
    private val repository = VideoRepository()
    
    private val _sections = MutableLiveData<List<HomeSection>>()
    val sections: LiveData<List<HomeSection>> = _sections
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // 进程级内存缓存：切换 Tab 会重建 ViewModel，用 companion 持有可跨 Tab 复用，避免每次重复请求 5 个分类
    private companion object {
        const val CACHE_TTL_MS = 10 * 60 * 1000L // 10 分钟
        var cachedSections: List<HomeSection>? = null
        var cacheLoadedAt: Long = 0
    }

    /**
     * 同步 Web 端首页推荐逻辑
     * 从分类中获取推荐内容，并构建分区展示
     */
    fun loadHomeVideos(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            // 缓存未过期且非强制刷新时直接复用，避免重复请求
            val now = System.currentTimeMillis()
            if (!forceRefresh && cachedSections != null && now - cacheLoadedAt < CACHE_TTL_MS) {
                _sections.value = cachedSections
                _isLoading.value = false
                return@launch
            }

            _isLoading.value = true
            _error.value = null

            // 定义分类：typeId 和显示名称
            val categories = listOf(
                Pair(1, "热播电影"),
                Pair(2, "热播连续剧"),
                Pair(3, "热播综艺"),
                Pair(4, "热播动漫"),
                Pair(5, "热播短剧")
            )

            // 并发请求各分类，加快首页加载
            val sectionList = categories.map { (typeId, name) ->
                async {
                    Triple(name, typeId, repository.getCategoryVideos(typeId = typeId, page = 1))
                }
            }.awaitAll().mapNotNull { (name, typeId, result) ->
                result.getOrNull()?.takeIf { it.isNotEmpty() }?.let { videos ->
                    // 每个分类取最新 6 个：优先展示热门和新更新的视频
                    val sortedVideos = videos.sortedWith(
                        compareByDescending<Video> { it.vod_time_add } // 按添加时间排序
                            .thenByDescending { it.vod_hits }           // 其次按点击量排序
                    ).take(6)
                    HomeSection(name, typeId, sortedVideos)
                }
            }

            if (sectionList.isEmpty()) {
                _error.value = "加载首页数据失败"
            } else {
                cachedSections = sectionList
                cacheLoadedAt = System.currentTimeMillis()
                _sections.value = sectionList
            }

            _isLoading.value = false
        }
    }
}
