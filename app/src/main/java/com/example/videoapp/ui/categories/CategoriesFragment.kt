package com.example.videoapp.ui.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videoapp.R
import com.example.videoapp.data.model.Category
import com.example.videoapp.databinding.FragmentCategoriesBinding
import com.example.videoapp.ui.home.VideoAdapter
import com.example.videoapp.ui.player.VideoPlayerActivity

class CategoriesFragment : Fragment() {
    
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val TAG = "CategoriesFragment"
    
    private val viewModel: CategoriesViewModel by viewModels()
    
    private var unifiedTypeId: Int = 0
    private var currentChildTypeId: Int = 0
    
    // 当前大类的子分类列表（动态从 API 加载）
    private var currentChildCategories: List<Pair<Int, String>> = listOf(Pair(0, "全部"))
    
    companion object {
        private const val ARG_TYPE_ID = "type_id"
        
        fun newInstance(typeId: Int): CategoriesFragment {
            val fragment = CategoriesFragment()
            val args = Bundle()
            args.putInt(ARG_TYPE_ID, typeId)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            unifiedTypeId = it.getInt(ARG_TYPE_ID, 0)
            currentChildTypeId = 0
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        Log.d(TAG, "Fragment onViewCreated, typeId: $unifiedTypeId")
        
        setupRecyclerView()
        observeViewModel()
        
        // 加载分类列表并构建子导航
        viewModel.loadCategories()
        
        // 加载该大类的子类视频（默认加载"全部"）
        if (unifiedTypeId > 0) {
            viewModel.loadCategoryVideos(unifiedTypeId, 0)
        }
    }
    
    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView")
        binding.recyclerViewCategories.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewCategories.adapter = VideoAdapter { video ->
            try {
                Log.d(TAG, "Video clicked: ${video.vod_name}")
                
                // 传递完整视频数据到详情页，包含所有渠道的播放源信息
                val intent = android.content.Intent(requireContext(), com.example.videoapp.ui.detail.VideoDetailActivity::class.java)
                intent.putExtra(com.example.videoapp.ui.detail.VideoDetailActivity.EXTRA_VIDEO_ID, video.vod_id.toString())
                intent.putExtra(com.example.videoapp.ui.detail.VideoDetailActivity.EXTRA_VIDEO_TITLE, video.vod_name)
                
                // 序列化完整视频数据（包含播放源信息）
                val videoJson = com.google.gson.Gson().toJson(video)
                intent.putExtra(com.example.videoapp.ui.detail.VideoDetailActivity.EXTRA_VIDEO_DATA, videoJson)
                
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Error opening video player", e)
                Toast.makeText(requireContext(), "打开播放器失败：${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupChildNavigation() {
        val container = binding.childNavContainer
        container.removeAllViews()
        
        // 使用动态加载的子分类，如果没有则使用默认"全部"
        val children = currentChildCategories
        
        children.forEachIndexed { index, pair ->
            val (childTypeId, name) = pair
            val itemView = LayoutInflater.from(requireContext()).inflate(
                R.layout.item_nav_category, container, false
            )
            val textView = itemView.findViewById<TextView>(R.id.text_view_nav_item)
            textView.text = name
            
            // 设置默认选中"全部"
            if (index == 0) {
                itemView.isSelected = true
                textView.setTextColor(resources.getColor(android.R.color.white, requireContext().theme))
            }
            
            itemView.setOnClickListener {
                // 更新选中状态
                for (i in 0 until container.childCount) {
                    val child = container.getChildAt(i)
                    child.isSelected = false
                    child.findViewById<TextView>(R.id.text_view_nav_item).setTextColor(
                        resources.getColor(R.color.primary, requireContext().theme)
                    )
                }
                itemView.isSelected = true
                textView.setTextColor(resources.getColor(android.R.color.white, requireContext().theme))
                
                // 加载对应子类视频
                currentChildTypeId = childTypeId
                viewModel.loadCategoryVideos(unifiedTypeId, childTypeId)
            }
            
            container.addView(itemView)
        }
    }
    
    /**
     * 解析第一个剧集的播放 URL
     */
    private fun parseFirstEpisodeUrl(playUrl: String): String {
        if (playUrl.isEmpty()) {
            return ""
        }
        try {
            val episodes = playUrl.split("#")
            if (episodes.isNotEmpty()) {
                val firstEpisode = episodes[0]
                val parts = firstEpisode.split("$")
                if (parts.size >= 2) {
                    return parts[1]
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing play URL", e)
        }
        return ""
    }
    
    private fun observeViewModel() {
        // 观察分类列表数据，构建子导航
        viewModel.hierarchicalCategories.observe(viewLifecycleOwner) { hierarchicalCats ->
            hierarchicalCats?.let { cats ->
                // 找到当前大类对应的子分类
                val parentCategory = cats.find { it.type_id == unifiedTypeId }
                if (parentCategory != null && parentCategory.children.isNotEmpty()) {
                    // 有子分类，构建导航列表
                    currentChildCategories = listOf(Pair(0, "全部")) + parentCategory.children.map { 
                        Pair(it.type_id, it.type_name) 
                    }
                    setupChildNavigation()
                } else {
                    // 没有子分类，只显示"全部"
                    currentChildCategories = listOf(Pair(0, "全部"))
                    setupChildNavigation()
                }
            }
        }
        
        // 观察分类视频数据
        viewModel.videos.observe(viewLifecycleOwner) { videos ->
            videos?.let {
                (binding.recyclerViewCategories.adapter as? VideoAdapter)?.submitList(it)
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarCategories.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), "加载失败：$it", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
