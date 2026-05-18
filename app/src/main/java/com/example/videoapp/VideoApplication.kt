package com.example.videoapp

import android.app.Application

class VideoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: VideoApplication
            private set
    }
}
