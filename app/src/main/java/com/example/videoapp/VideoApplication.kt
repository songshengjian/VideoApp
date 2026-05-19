package com.example.videoapp

import android.app.Application
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class VideoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 设置全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("VideoApp", "Uncaught exception in thread ${thread.name}", throwable)
            
            // 保存崩溃日志到文件
            try {
                val logFile = File(cacheDir, "crash_log.txt")
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                throwable.printStackTrace(pw)
                logFile.writeText("""
                    ====== Crash Report ======
                    Thread: ${thread.name}
                    Time: ${System.currentTimeMillis()}
                    
                    ${sw.toString()}
                """.trimIndent())
                Log.e("VideoApp", "Crash log saved to: ${logFile.absolutePath}")
            } catch (e: Exception) {
                Log.e("VideoApp", "Failed to save crash log", e)
            }
        }
        
        Log.d("VideoApp", "Application started")
    }
}
