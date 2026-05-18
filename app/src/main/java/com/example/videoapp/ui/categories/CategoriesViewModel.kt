package com.example.videoapp.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoapp.data.model.Category
import com.example.videoapp.data.repository.VideoRepository
import kotlinx.coroutines.launch

class CategoriesViewModel : ViewModel() {
    
    private val repository = VideoRepository()
    
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            
            repository.getCategories()
                .onSuccess { categories ->
                    _categories.value = categories
                }
                .onFailure { exception ->
                    // 处理错误
                }
            
            _isLoading.value = false
        }
    }
}
