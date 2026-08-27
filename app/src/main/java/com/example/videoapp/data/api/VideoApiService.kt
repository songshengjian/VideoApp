package com.example.videoapp.data.api

import com.example.videoapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface VideoApiService {
    
    /**
     * 获取首页推荐视频
     * Web 端对应接口：IndexController::getVideoList
     */
    @GET("/api/video/list")
    suspend fun getHomeVideos(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<VideoResponse>
    
    /**
     * 获取分类列表（支持层级分类）
     * Web 端对应接口：IndexController::getVideoTypes
     */
    @GET("/api/video/type")
    suspend fun getCategories(): Response<CategoryResponse>
    
    /**
     * 获取分类视频列表
     * Web 端对应接口：IndexController::getVideoList
     */
    @GET("/api/video/list")
    suspend fun getCategoryVideos(
        @Query("type_id") typeId: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<VideoResponse>
    
    /**
     * 搜索视频（多频道聚合搜索）
     * Web 端对应接口：IndexController::searchVideos
     */
    @GET("/api/video/search")
    suspend fun searchVideos(
        @Query("wd") keyword: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 100
    ): Response<VideoSearchResponse>
    
    /**
     * 获取视频详情（单频道）
     * Web 端对应接口：IndexController::getVideoDetail
     */
    @GET("/api/video/detail")
    suspend fun getVideoDetail(
        @Query("ids") ids: String
    ): Response<VideoResponse>
    
    /**
     * 获取视频详情（所有频道聚合）
     * Web 端对应接口：IndexController::getVideoDetailAllChannels
     */
    @GET("/api/video/detail-all")
    suspend fun getVideoDetailAllChannels(
        @Query("ids") ids: String
    ): Response<VideoResponse>
    
    /**
     * 获取广告配置
     * Web 端对应接口：IndexController::getAds
     */
    @GET("/api/ads")
    suspend fun getAds(): Response<AdsResponse>
}
