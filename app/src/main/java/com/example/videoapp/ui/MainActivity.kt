package com.example.videoapp.ui

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.videoapp.R
import com.example.videoapp.databinding.ActivityMainBinding
import com.example.videoapp.databinding.LayoutHeaderBinding
import com.example.videoapp.ui.categories.CategoriesFragment
import com.example.videoapp.ui.home.HomeFragment
import com.example.videoapp.ui.profile.ProfileFragment
import com.example.videoapp.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var headerBinding: LayoutHeaderBinding
    private val TAG = "MainActivity"

    // 当前选中的 Fragment
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // 获取 Header Binding
            headerBinding = binding.header

            setupHeader()
            setupBottomNavigation()

            // 默认显示首页
            if (savedInstanceState == null) {
                switchFragment(HomeFragment())
                binding.bottomNavigation.selectedItemId = R.id.nav_home
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, "启动失败：${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupHeader() {
        try {
            // 搜索框提交：跳转到搜索页
            headerBinding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val keyword = headerBinding.editTextSearch.text.toString().trim()
                    if (keyword.isNotEmpty()) {
                        openSearch(keyword)
                    }
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupHeader", e)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    switchFragment(HomeFragment())
                    true
                }
                R.id.nav_categories -> {
                    switchFragment(CategoriesFragment.newInstance(0))
                    true
                }
                R.id.nav_search -> {
                    switchFragment(SearchFragment.newInstance(""))
                    true
                }
                R.id.nav_profile -> {
                    switchFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    /** 顶部搜索框提交：切到搜索 Tab 并执行搜索 */
    fun openSearch(keyword: String) {
        headerBinding.editTextSearch.clearFocus()
        binding.bottomNavigation.selectedItemId = R.id.nav_search
        switchFragment(SearchFragment.newInstance(keyword))
    }

    private fun switchFragment(fragment: Fragment) {
        if (fragment != currentFragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            currentFragment = fragment
        }
    }
}
