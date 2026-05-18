package com.example.videoapp.data.repository

import com.example.videoapp.data.api.ApiClient
import com.example.videoapp.data.model.*
import java.lang.Exception

class VideoRepository {
    
    private val api = ApiClient.videoApi
    
    suspend fun getHomeVideos(): Result<List<Video>> {
        return try {
            val response = api.getHome()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.list)
            } else {
                Result.failure(Exception("获取首页数据失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.list)
            } else {
                Result.failure(Exception("获取分类失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCategoryVideos(typeId: Int, page: Int = 1): Result<List<Video>> {
        return try {
            val response = api.getCategoryVideos(typeId = typeId, page = page)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.list)
            } else {
                Result.failure(Exception("获取视频列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchVideos(keyword: String, page: Int = 1): Result<List<Video>> {
        return try {
            val response = api.searchVideos(keyword = keyword, page = page)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.list)
            } else {
                Result.failure(Exception("搜索失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getVideoDetail(ids: String): Result<Video?> {
        return try {
            val response = api.getVideoDetail(ids = ids)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.list.firstOrNull())
            } else {
                Result.failure(Exception("获取视频详情失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(username: String, password: String): Result<User> {
        return try {
            val response = api.login(username = username, password = password)
            if (response.isSuccessful && response.body() != null) {
                // 实际项目中需要根据返回的数据构造 User 对象
                Result.success(User(user_id = 1, user_name = username))
            } else {
                Result.failure(Exception("登录失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(username: String, password: String): Result<User> {
        return try {
            val response = api.register(username = username, password = password)
            if (response.isSuccessful && response.body() != null) {
                Result.success(User(user_id = 1, user_name = username))
            } else {
                Result.failure(Exception("注册失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
