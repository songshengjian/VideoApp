package com.example.videoapp.data.api

import com.example.videoapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface VideoApiService {
    
    @GET("/")
    suspend fun getHome(
        @Header("ac") ac: String = "detail"
    ): Response<VideoResponse>
    
    @GET("")
    suspend fun getCategories(
        @Query("ac") ac: String = "type"
    ): Response<CategoryResponse>
    
    @GET("")
    suspend fun getCategoryVideos(
        @Query("ac") ac: String = "detail",
        @Query("t") typeId: Int,
        @Query("pg") page: Int = 1,
        @Query("h") home: String = "1"
    ): Response<VideoResponse>
    
    @GET("")
    suspend fun searchVideos(
        @Query("ac") ac: String = "detail",
        @Query("wd") keyword: String,
        @Query("pg") page: Int = 1
    ): Response<VideoResponse>
    
    @GET("")
    suspend fun getVideoDetail(
        @Query("ac") ac: String = "detail",
        @Query("ids") ids: String
    ): Response<VideoResponse>
    
    @POST("/index/video")
    suspend fun getPlayUrl(
        @FormUrlEncoded
        @Field("ids") ids: String,
        @Field("channel_url") channelUrl: String = ""
    ): Response<VideoResponse>
    
    @POST("/admin/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("user_name") username: String,
        @Field("user_pwd") password: String
    ): Response<VideoResponse>
    
    @POST("/index/user/reg")
    @FormUrlEncoded
    suspend fun register(
        @Field("user_name") username: String,
        @Field("user_pwd") password: String,
        @Field("user_email") email: String = "",
        @Field("user_phone") phone: String = ""
    ): Response<VideoResponse>
}
