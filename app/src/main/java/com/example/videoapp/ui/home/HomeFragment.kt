package com.example.videoapp.ui.home

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
import com.example.videoapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val TAG = "HomeFragment"
    
    private val viewModel: HomeViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Fragment onCreate")
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "Fragment onCreateView")
        try {
            _binding = FragmentHomeBinding.inflate(inflater, container, false)
            Log.d(TAG, "Binding inflated successfully")
            return binding.root
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreateView", e)
            Toast.makeText(requireContext(), "页面加载失败：${e.message}", Toast.LENGTH_SHORT).show()
            throw e
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Fragment onViewCreated")
        
        try {
            setupRecyclerView()
            Log.d(TAG, "RecyclerView setup complete")
            
            observeViewModel()
            Log.d(TAG, "ViewModel observation setup complete")
            
            viewModel.loadHomeVideos()
            Log.d(TAG, "Loading videos")
        } catch (e: Exception) {
            Log.e(TAG, "Error in onViewCreated", e)
            Toast.makeText(requireContext(), "加载失败：${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView")
        binding.recyclerViewHome.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewHome.adapter = VideoAdapter { video ->
            try {
                Log.d(TAG, "Video clicked: ${video.vod_name}, vod_id: ${video.vod_id}")
                
                // 获取播放 URL
                val playUrl = parseFirstEpisodeUrl(video.vod_play_url)
                Log.d(TAG, "Parsed play URL: $playUrl")
                
                if (playUrl.isEmpty()) {
                    Toast.makeText(requireContext(), "该视频暂无播放资源", Toast.LENGTH_SHORT).show()
                    return@VideoAdapter
                }
                
                // 跳转到视频播放页面
                val intent = android.content.Intent(requireContext(), com.example.videoapp.ui.player.VideoPlayerActivity::class.java)
                intent.putExtra(com.example.videoapp.ui.player.VideoPlayerActivity.EXTRA_VIDEO_ID, video.vod_id.toString())
                intent.putExtra(com.example.videoapp.ui.player.VideoPlayerActivity.EXTRA_VIDEO_TITLE, video.vod_name)
                // 先传递第一集的 URL 用于立即播放
                val firstUrl = parseFirstEpisodeUrl(video.vod_play_url)
                intent.putExtra(com.example.videoapp.ui.player.VideoPlayerActivity.EXTRA_VIDEO_URL, firstUrl)
                Log.d(TAG, "Starting VideoPlayerActivity with URL: $firstUrl")
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Error opening video player", e)
                Toast.makeText(requireContext(), "打开播放器失败：${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        Log.d(TAG, "RecyclerView adapter set")
    }
    
    /**
     * 解析第一个剧集的播放 URL
     * 格式：剧集 1$URL1#剧集 2$URL2#...
     */
    private fun parseFirstEpisodeUrl(playUrl: String): String {
        Log.d(TAG, "Parsing play URL: ${playUrl.take(100)}...")
        if (playUrl.isEmpty()) {
            Log.w(TAG, "Play URL is empty")
            return ""
        }
        try {
            // 按 # 分割剧集
            val episodes = playUrl.split("#")
            Log.d(TAG, "Found ${episodes.size} episodes")
            if (episodes.isNotEmpty()) {
                // 取第一集，格式为 "剧集名$URL"
                val firstEpisode = episodes[0]
                Log.d(TAG, "First episode: $firstEpisode")
                val parts = firstEpisode.split("$")
                Log.d(TAG, "Split into ${parts.size} parts")
                if (parts.size >= 2) {
                    val url = parts[1]
                    Log.d(TAG, "Extracted URL: ${url.take(100)}...")
                    return url // 返回 URL 部分
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing play URL", e)
        }
        return ""
    }
    
    private fun observeViewModel() {
        Log.d(TAG, "Observing ViewModel")
        viewModel.videos.observe(viewLifecycleOwner, Observer { videos ->
            Log.d(TAG, "Videos received: ${videos?.size}")
            videos?.let {
                (binding.recyclerViewHome.adapter as? VideoAdapter)?.submitList(it)
            }
        })
        
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            Log.d(TAG, "Is loading: $isLoading")
            binding.progressBarHome.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
        
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            Log.d(TAG, "Error observed: $error")
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "Fragment onDestroyView")
        _binding = null
    }
}
