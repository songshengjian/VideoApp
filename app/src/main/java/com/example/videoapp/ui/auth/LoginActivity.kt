package com.example.videoapp.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.videoapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // TODO: 实现登录逻辑
    }
}
