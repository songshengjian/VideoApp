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
    
    // 分类导航项
    private val navCategories = listOf(
        Pair(0, "首页"),
        Pair(1, "电影"),
        Pair(2, "连续剧"),
        Pair(3, "综艺"),
        Pair(4, "动漫"),
        Pair(5, "短剧")
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "Activity started")
            binding = ActivityMainBinding.inflate(layoutInflater)
            Log.d(TAG, "Binding inflated")
            setContentView(binding.root)
            Log.d(TAG, "Content view set")
            
            // 获取 Header Binding
            headerBinding = binding.header
            
            setupHeader()
            Log.d(TAG, "Header setup complete")
            
            // 默认显示首页
            if (savedInstanceState == null) {
                switchFragment(HomeFragment(), 0)
            }
            Log.d(TAG, "Navigation setup complete")
            
            Toast.makeText(this, "畅观启动成功！", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, "启动失败：${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun setupHeader() {
        try {
            // 设置分类导航
            val container = headerBinding.navCategoriesContainer
            container.removeAllViews()
            
            navCategories.forEachIndexed { index, pair ->
                val (typeId, name) = pair
                val itemView = layoutInflater.inflate(R.layout.item_nav_category, container, false)
                itemView.findViewById<android.widget.TextView>(R.id.text_view_nav_item).text = name
                
                // 设置选中状态
                if (index == 0) {
                    itemView.isSelected = true
                    itemView.findViewById<android.widget.TextView>(R.id.text_view_nav_item).setTextColor(
                        resources.getColor(android.R.color.white, theme)
                    )
                }
                
                itemView.setOnClickListener {
                    // 更新选中状态
                    for (i in 0 until container.childCount) {
                        val child = container.getChildAt(i)
                        child.isSelected = false
                        child.findViewById<android.widget.TextView>(R.id.text_view_nav_item).setTextColor(
                            resources.getColor(R.color.primary, theme)
                        )
                    }
                    itemView.isSelected = true
                    itemView.findViewById<android.widget.TextView>(R.id.text_view_nav_item).setTextColor(
                        resources.getColor(android.R.color.white, theme)
                    )
                    
                    // 切换 Fragment
                    switchFragmentForCategory(typeId)
                }
                
                container.addView(itemView)
            }
            
            // 设置搜索框
            headerBinding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val keyword = headerBinding.editTextSearch.text.toString().trim()
                    if (keyword.isNotEmpty()) {
                        val searchFragment = SearchFragment.newInstance(keyword)
                        switchFragment(searchFragment, -1)
                        
                        // 取消搜索框焦点
                        headerBinding.editTextSearch.clearFocus()
                    }
                    true
                } else {
                    false
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupHeader", e)
            throw e
        }
    }
    
    private fun switchFragmentForCategory(typeId: Int) {
        val fragment = when (typeId) {
            0 -> HomeFragment()
            else -> CategoriesFragment.newInstance(typeId)
        }
        switchFragment(fragment, typeId)
    }
    
    private fun switchFragment(fragment: Fragment, typeId: Int) {
        if (fragment != currentFragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            currentFragment = fragment
        }
    }
}
