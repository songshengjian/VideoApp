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
    
    // 大类对应的子类列表
    private val childCategories = mapOf(
        1 to listOf(Pair(0, "全部"), Pair(101, "动作片"), Pair(102, "喜剧片"), Pair(103, "爱情片"), Pair(104, "科幻片"), Pair(105, "恐怖片"), Pair(106, "剧情片"), Pair(107, "战争片"), Pair(108, "纪录片")),
        2 to listOf(Pair(0, "全部"), Pair(201, "国产剧"), Pair(202, "港台剧"), Pair(203, "日韩剧"), Pair(204, "欧美剧"), Pair(205, "其他剧")),
        3 to listOf(Pair(0, "全部"), Pair(301, "大陆综艺"), Pair(302, "港台综艺"), Pair(303, "日韩综艺"), Pair(304, "欧美综艺")),
        4 to listOf(Pair(0, "全部"), Pair(401, "国产动漫"), Pair(402, "日韩动漫"), Pair(403, "欧美动漫"), Pair(404, "港台动漫"), Pair(405, "其他动漫")),
        5 to listOf(Pair(0, "全部"), Pair(501, "古装短剧"), Pair(502, "现代短剧"), Pair(503, "都市短剧"))
    )
    
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
        setupChildNavigation()
        observeViewModel()
        
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
                
                // 获取播放 URL
                val playUrl = parseFirstEpisodeUrl(video.vod_play_url)
                
                if (playUrl.isEmpty()) {
                    Toast.makeText(requireContext(), "该视频暂无播放资源", Toast.LENGTH_SHORT).show()
                    return@VideoAdapter
                }
                
                // 跳转到视频播放页面
                val intent = android.content.Intent(requireContext(), VideoPlayerActivity::class.java)
                intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_ID, video.vod_id.toString())
                intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_TITLE, video.vod_name)
                intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, playUrl)
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
        
        val children = childCategories[unifiedTypeId] ?: listOf(Pair(0, "全部"))
        
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
        // 观察分类视频数据
        viewModel.videos.observe(viewLifecycleOwner, Observer { videos ->
            videos?.let {
                (binding.recyclerViewCategories.adapter as? VideoAdapter)?.submitList(it)
            }
        })
        
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBarCategories.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
        
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(requireContext(), "加载失败：$it", Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
