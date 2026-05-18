package com.example.videoapp.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.videoapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegisterBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // TODO: 实现注册逻辑
    }
}
