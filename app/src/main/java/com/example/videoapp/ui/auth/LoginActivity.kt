package com.example.videoapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.videoapp.databinding.ActivityLoginBinding
import com.example.videoapp.ui.MainActivity

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupClickListeners() {
        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            
            if (username.isEmpty()) {
                binding.textInputLayoutUsername.error = "请输入用户名"
                return@setOnClickListener
            }
            
            if (password.isEmpty()) {
                binding.textInputLayoutPassword.error = "请输入密码"
                return@setOnClickListener
            }
            
            viewModel.login(username, password)
        }
        
        binding.textviewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarLogin.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            binding.buttonLogin.isEnabled = !isLoading
        }
        
        viewModel.loginResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
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
