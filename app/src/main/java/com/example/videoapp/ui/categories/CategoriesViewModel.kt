package com.example.videoapp.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoapp.data.model.Category
import com.example.videoapp.data.model.Video
import com.example.videoapp.data.repository.VideoRepository
import kotlinx.coroutines.launch

class CategoriesViewModel : ViewModel() {
    
    private val repository = VideoRepository()
    
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories
    
    private val _hierarchicalCategories = MutableLiveData<List<Category>>()
    val hierarchicalCategories: LiveData<List<Category>> = _hierarchicalCategories
    
    private val _videos = MutableLiveData<List<Video>>()
    val videos: LiveData<List<Video>> = _videos
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * 加载分类列表（支持层级分类）
     */
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getCategories()
                .onSuccess { response ->
                    _categories.value = response.class_
                    // 同步层级分类结构（Web 端有 tree 字段）
                    // 这里使用 parentId 字段来构建层级
                    _hierarchicalCategories.value = buildHierarchicalCategories(response.class_)
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }
    
    fun loadCategoryVideos(typeId: Int, childTypeId: Int = 0) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            // 如果 childTypeId 为 0，使用大类的 typeId
            val actualTypeId = if (childTypeId > 0) childTypeId else typeId
            
            repository.getCategoryVideos(typeId = actualTypeId, page = 1)
                .onSuccess { videos ->
                    _videos.value = videos
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }
    
    /**
     * 构建层级分类结构
     */
    private fun buildHierarchicalCategories(flatCategories: List<Category>): List<Category> {
        val parentMap = mutableMapOf<Int, Category>()
        val childrenMap = mutableMapOf<Int, MutableList<Category>>()
        
        // 分离父分类和子分类
        flatCategories.forEach { category ->
            // 父分类：parent_id == 0 或 type_pid == 0
            val isParent = category.parent_id == 0 && category.type_pid == 0
            if (isParent) {
                // 这是父分类（大类）
                parentMap[category.type_id] = category.copy(children = mutableListOf())
            } else {
                // 这是子分类，找到它的父分类 ID
                val parentId = if (category.parent_id > 0) category.parent_id else category.type_pid
                if (parentId > 0) {
                    childrenMap.getOrPut(parentId) { mutableListOf() }.add(category)
                }
            }
        }
        
        // 将子分类添加到父分类的 children 列表中
        parentMap.forEach { (parentId, parent) ->
            childrenMap[parentId]?.let { children ->
                parentMap[parentId] = parent.copy(children = children)
            }
        }
        
        return parentMap.values.toList()
    }
}
