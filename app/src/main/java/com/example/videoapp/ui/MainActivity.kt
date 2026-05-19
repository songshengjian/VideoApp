package com.example.videoapp.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.videoapp.R
import com.example.videoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "Activity started")
            binding = ActivityMainBinding.inflate(layoutInflater)
            Log.d(TAG, "Binding inflated")
            setContentView(binding.root)
            Log.d(TAG, "Content view set")
            
            setupNavigation()
            Log.d(TAG, "Navigation setup complete")
            
            Toast.makeText(this, "应用启动成功！", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, "启动失败：${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun setupNavigation() {
        try {
            Log.d(TAG, "Setting up navigation")
            
            val navHostFragment = NavHostFragment.create(R.navigation.nav_graph)
            Log.d(TAG, "NavHostFragment created: $navHostFragment")
            
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, navHostFragment)
                .setPrimaryNavigationFragment(navHostFragment)
                .commit()
            
            // 等待 fragment 被 attach 后再获取 navController
            supportFragmentManager.executePendingTransactions()
            Log.d(TAG, "Fragment transaction committed and executed")
            
            val navController = navHostFragment.navController
            Log.d(TAG, "NavController obtained: $navController")
            
            binding.bottomNavigation.setupWithNavController(navController)
            Log.d(TAG, "Bottom navigation setup complete")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupNavigation", e)
            throw e
        }
    }
}
