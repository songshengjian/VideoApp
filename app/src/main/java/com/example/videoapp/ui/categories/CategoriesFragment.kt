package com.example.videoapp.ui.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videoapp.R
import com.example.videoapp.databinding.FragmentCategoriesBinding
import com.example.videoapp.ui.home.VideoAdapter
import com.example.videoapp.ui.player.VideoPlayerActivity

class CategoriesFragment : Fragment() {
    
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val TAG = "CategoriesFragment"
    
    private val viewModel: CategoriesViewModel by viewModels()
    
    private var unifiedTypeId: Int = 0
    
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
        
        // 根据 typeId 加载对应分类视频
        if (unifiedTypeId > 0) {
            viewModel.loadCategoryVideos(unifiedTypeId)
        } else {
            viewModel.loadCategories()
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
