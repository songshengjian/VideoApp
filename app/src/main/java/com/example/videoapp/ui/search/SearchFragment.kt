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
import com.example.videoapp.ui.detail.VideoDetailActivity

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
                
                // 跳转到视频详情页面
                val intent = android.content.Intent(requireContext(), VideoDetailActivity::class.java)
                intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_ID, video.vod_id.toString())
                intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_TITLE, video.vod_name)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Error opening video detail", e)
                Toast.makeText(requireContext(), "打开详情页失败：${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.videos.observe(viewLifecycleOwner) { videos ->
            videos.let {
                (binding.recyclerViewSearch.adapter as? VideoAdapter)?.submitList(it)
            }
        }
        
        viewModel.searchedChannels.observe(viewLifecycleOwner) { channels ->
            channels.let {
                if (it.isNotEmpty()) {
                    val channelInfo = "已搜索 ${it.size} 个频道：${it.joinToString("、").take(50)}${if (it.size > 5) "等" else ""}"
                    binding.textViewSearchInfo.text = channelInfo
                    binding.textViewSearchInfo.visibility = View.VISIBLE
                } else {
                    binding.textViewSearchInfo.visibility = View.GONE
                }
            }
        }
        
        viewModel.total.observe(viewLifecycleOwner) { total ->
            if (total > 0) {
                binding.textViewResultCount.text = "共找到 $total 个结果"
                binding.textViewResultCount.visibility = View.VISIBLE
            } else {
                binding.textViewResultCount.visibility = View.GONE
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarSearch.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
