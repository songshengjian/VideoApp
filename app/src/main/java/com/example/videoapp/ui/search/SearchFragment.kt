package com.example.videoapp.ui.search

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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videoapp.R
import com.example.videoapp.databinding.FragmentSearchBinding
import com.example.videoapp.ui.home.VideoAdapter
import com.example.videoapp.ui.detail.VideoDetailActivity
import com.google.gson.Gson

class SearchFragment : Fragment() {
    
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val TAG = "SearchFragment"
    
    private val viewModel: SearchViewModel by viewModels()
    
    private var searchKeyword: String = ""
    private var currentChannel: String = "全部"
    private var allVideos: List<com.example.videoapp.data.model.Video> = emptyList()
    private val searchedChannels = mutableListOf<String>()
    
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
        
        binding.textViewKeyword.text = "搜索：$searchKeyword"
        
        setupRecyclerView()
        setupChannelFilter()
        observeViewModel()
        
        if (searchKeyword.isNotEmpty()) {
            viewModel.searchVideos(searchKeyword)
        }
    }
    
    private fun setupRecyclerView() {
        binding.recyclerViewSearch.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewSearch.adapter = VideoAdapter { video ->
            try {
                // 直接传递完整视频数据，包含播放源
                val intent = android.content.Intent(requireContext(), VideoDetailActivity::class.java)
                intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_ID, video.vod_id.toString())
                intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_TITLE, video.vod_name)
                
                val videoJson = Gson().toJson(video)
                intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_DATA, videoJson)
                
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Error opening video detail", e)
                Toast.makeText(requireContext(), "打开详情页失败：${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupChannelFilter() {
        binding.buttonChannelAll.setOnClickListener {
            if (currentChannel != "全部") {
                currentChannel = "全部"
                updateChannelFilterUI()
                filterVideos()
            }
        }
    }
    
    private fun createChannelButtons(channels: List<String>) {
        binding.layoutChannelButtons.removeAllViews()
        
        channels.forEach { channel ->
            val textView = TextView(requireContext()).apply {
                text = channel
                setPadding(24, 16, 24, 16)
                textSize = 12f
                setTextColor(resources.getColor(android.R.color.white, context.theme))
                setBackgroundResource(R.drawable.channel_filter_bg)
                isSelected = false
                setOnClickListener {
                    if (currentChannel != channel) {
                        currentChannel = channel
                        updateChannelFilterUI()
                        filterVideos()
                    }
                }
            }
            
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            textView.layoutParams = params
            
            binding.layoutChannelButtons.addView(textView)
        }
    }
    
    private fun updateChannelFilterUI() {
        binding.buttonChannelAll.isSelected = (currentChannel == "全部")
        
        for (i in 0 until binding.layoutChannelButtons.childCount) {
            val view = binding.layoutChannelButtons.getChildAt(i)
            if (view is TextView) {
                view.isSelected = (view.text.toString() == currentChannel)
            }
        }
    }
    
    private fun filterVideos() {
        val filtered = if (currentChannel == "全部") {
            allVideos
        } else {
            allVideos.filter { it._channel_name == currentChannel }
        }
        
        (binding.recyclerViewSearch.adapter as? VideoAdapter)?.submitList(filtered)
        binding.textViewResultCount.text = "共找到 ${filtered.size} 个结果（${currentChannel}）"
    }
    
    private fun observeViewModel() {
        viewModel.videos.observe(viewLifecycleOwner) { videos ->
            videos?.let {
                allVideos = it
                filterVideos()
                
                // 显示搜索渠道信息（使用 Web 端返回的真实渠道归属）
                if (it.isNotEmpty()) {
                    val channels = it.mapNotNull { v -> v._channel_name }
                        .filter { it.isNotEmpty() }
                        .distinct()
                    
                    if (channels.isNotEmpty()) {
                        searchedChannels.clear()
                        searchedChannels.addAll(channels)
                        
                        binding.layoutChannelFilter.visibility = View.VISIBLE
                        createChannelButtons(channels)
                    }
                }
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarSearch.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), "搜索失败：$it", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
