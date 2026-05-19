package com.example.videoapp.data.api

import com.example.videoapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface VideoApiService {
    
    @GET("/api/video/list")
    suspend fun getHomeVideos(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<VideoResponse>
    
    @GET("/api/video/type")
    suspend fun getCategories(): Response<CategoryResponse>
    
    @GET("/api/video/list")
    suspend fun getCategoryVideos(
        @Query("type_id") typeId: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<VideoResponse>
    
    @GET("/api/video/search")
    suspend fun searchVideos(
        @Query("wd") keyword: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<VideoResponse>
    
    @GET("/api/video/detail")
    suspend fun getVideoDetail(
        @Query("ids") ids: String
    ): Response<VideoResponse>
    
    @POST("/api/user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("user_name") username: String,
        @Field("user_pwd") password: String
    ): Response<LoginResponse>
    
    @POST("/api/user/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("user_name") username: String,
        @Field("user_pwd") password: String,
        @Field("user_email") email: String = ""
    ): Response<LoginResponse>
}

data class LoginResponse(
    val code: Int,
    val msg: String,
    val data: LoginData?
)

data class LoginData(
    val user_id: Int,
    val user_name: String,
    val token: String
)
