package com.example.videoapp.data.repository

import com.example.videoapp.data.api.ApiClient
import com.example.videoapp.data.model.*
import java.net.UnknownHostException
import java.net.SocketTimeoutException
import java.io.IOException

class VideoRepository {
    
    private val api = ApiClient.videoApi
    
    private fun handleNetworkError(e: Exception): Exception {
        return when (e) {
            is UnknownHostException -> Exception("无法连接到服务器，请检查网络连接")
            is SocketTimeoutException -> Exception("连接服务器超时，请稍后重试")
            is IOException -> Exception("网络连接失败，请检查网络设置")
            else -> Exception("网络异常：${e.message}")
        }
    }
    
    suspend fun getHomeVideos(): Result<List<Video>> {
        return try {
            val response = api.getHomeVideos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.list)
            } else {
                Result.failure(Exception("获取首页数据失败"))
            }
        } catch (e: Exception) {
            Result.failure(handleNetworkError(e))
        }
    }
    
    suspend fun getCategories(): Result<CategoryResponse> {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("获取分类失败：${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(handleNetworkError(e))
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
            Result.failure(handleNetworkError(e))
        }
    }
    
    /**
     * 搜索视频（多频道聚合）
     * 同步 Web 端 IndexController::searchVideos 逻辑
     */
    suspend fun searchVideos(keyword: String, page: Int = 1): Result<VideoSearchResponse> {
        return try {
            val response = api.searchVideos(keyword = keyword, page = page)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.code == 1) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body.msg))
                }
            } else {
                Result.failure(Exception("搜索失败：${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(handleNetworkError(e))
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
            Result.failure(handleNetworkError(e))
        }
    }
    
    /**
     * 获取所有频道的视频详情（聚合模式）
     * 同步 Web 端 IndexController::getVideoDetailAllChannels 逻辑
     * @param wd 视频名称：跨渠道按名称匹配，避免各渠道 ID 体系不同导致源错配
     */
    suspend fun getVideoDetailAllChannels(ids: String, wd: String = ""): Result<Video?> {
        return try {
            val response = api.getVideoDetailAllChannels(ids = ids, wd = wd)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.code == 1) {
                    Result.success(body.list.firstOrNull())
                } else {
                    Result.failure(Exception(body.msg))
                }
            } else {
                Result.failure(Exception("获取视频详情失败"))
            }
        } catch (e: Exception) {
            Result.failure(handleNetworkError(e))
        }
    }
    
    suspend fun getAds(): Result<AdsResponse> {
        return try {
            val response = api.getAds()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("获取广告配置失败"))
            }
        } catch (e: Exception) {
            Result.failure(handleNetworkError(e))
        }
    }
    
}
