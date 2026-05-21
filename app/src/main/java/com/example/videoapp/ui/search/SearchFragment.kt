package com.example.videoapp.ui.search

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
import com.example.videoapp.databinding.FragmentSearchBinding
import com.example.videoapp.ui.home.VideoAdapter
import com.example.videoapp.ui.player.VideoPlayerActivity

class SearchFragment : Fragment() {
    
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val TAG = "SearchFragment"
    
    private val viewModel: SearchViewModel by viewModels()
    
    private var searchKeyword: String = ""
    
    companion object {
        private const val ARG_KEYWORD = "keyword"
        
        fun newInstance(keyword: String): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()
            args.putString(ARG_KEYWORD, keyword)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchKeyword = it.getString(ARG_KEYWORD, "")
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        Log.d(TAG, "Fragment onViewCreated, keyword: $searchKeyword")
        
        setupRecyclerView()
        observeViewModel()
        
        // 如果有初始关键词，自动搜索
        if (searchKeyword.isNotEmpty()) {
            viewModel.searchVideos(searchKeyword)
        }
    }
    
    private fun setupRecyclerView() {
        binding.recyclerViewSearch.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewSearch.adapter = VideoAdapter { video ->
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
        viewModel.videos.observe(viewLifecycleOwner, Observer { videos ->
            videos?.let {
                (binding.recyclerViewSearch.adapter as? VideoAdapter)?.submitList(it)
            }
        })
        
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBarSearch.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
        
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
