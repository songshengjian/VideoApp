package com.example.videoapp.data.api

import com.example.videoapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    
    private const val BASE_URL = "https://videoweb-jncd.onrender.com"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // 仅 debug 构建记录完整请求/响应，release 不记录避免泄露播放地址等信息
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }
    
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * 播放器专用 OkHttpClient：不带日志拦截器（避免 debug 下把视频分片全部打进日志），
     * 复用连接池提升 HLS 分片加载速度。
     */
    val playerOkHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val videoApi: VideoApiService = retrofit.create(VideoApiService::class.java)
}
