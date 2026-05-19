package com.example.videoapp.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.videoapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupClickListeners() {
        binding.buttonRegister.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            
            if (username.isEmpty()) {
                binding.textInputLayoutUsername.error = "请输入用户名"
                return@setOnClickListener
            }
            
            if (username.length < 3) {
                binding.textInputLayoutUsername.error = "用户名至少 3 个字符"
                return@setOnClickListener
            }
            
            if (password.isEmpty()) {
                binding.textInputLayoutPassword.error = "请输入密码"
                return@setOnClickListener
            }
            
            if (password.length < 6) {
                binding.textInputLayoutPassword.error = "密码至少 6 个字符"
                return@setOnClickListener
            }
            
            if (password != confirmPassword) {
                binding.textInputLayoutConfirmPassword.error = "两次输入的密码不一致"
                return@setOnClickListener
            }
            
            viewModel.register(username, password, email)
        }
        
        binding.textviewLogin.setOnClickListener {
            finish()
        }
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarRegister.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            binding.buttonRegister.isEnabled = !isLoading
        }
        
        viewModel.registerResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "注册成功！请登录", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
