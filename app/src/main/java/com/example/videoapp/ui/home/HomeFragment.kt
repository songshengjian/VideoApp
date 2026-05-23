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
import com.example.videoapp.databinding.FragmentHomeBinding
import com.example.videoapp.ui.detail.VideoDetailActivity

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val TAG = "HomeFragment"
    
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var sectionAdapter: HomeSectionAdapter
    
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
            setupSectionAdapter()
            Log.d(TAG, "Section adapter setup complete")
            
            observeViewModel()
            Log.d(TAG, "ViewModel observation setup complete")
            
            viewModel.loadHomeVideos()
            Log.d(TAG, "Loading home videos")
        } catch (e: Exception) {
            Log.e(TAG, "Error in onViewCreated", e)
            Toast.makeText(requireContext(), "加载失败：${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupSectionAdapter() {
        Log.d(TAG, "Setting up section adapter")
        sectionAdapter = HomeSectionAdapter { video ->
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
        // 注意：这里我们不使用 RecyclerView，而是动态添加区块
    }
    
    private fun observeViewModel() {
        Log.d(TAG, "Observing ViewModel")
        viewModel.sections.observe(viewLifecycleOwner, Observer { sections ->
            Log.d(TAG, "Sections received: ${sections?.size}")
            sections?.let {
                renderSections(it)
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
    
    private fun renderSections(sections: List<HomeSection>) {
        val container = binding.homeSectionsContainer
        container.removeAllViews()
        
        sections.forEach { section ->
            // 动态创建区块视图
            val sectionView = layoutInflater.inflate(
                com.example.videoapp.R.layout.item_home_section,
                container,
                false
            )
            
            // 设置标题
            val titleView = sectionView.findViewById<android.widget.TextView>(
                com.example.videoapp.R.id.text_view_section_title
            )
            titleView.text = section.title
            
            // 设置横向视频列表
            val recyclerView = sectionView.findViewById<androidx.recyclerview.widget.RecyclerView>(
                com.example.videoapp.R.id.recycler_view_section_videos
            )
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                requireContext(),
                androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                false
            )
            
            val videoAdapter = HomeSectionVideoAdapter(section.videos) { video ->
                try {
                    Log.d(TAG, "Video clicked: ${video.vod_name}")
                    
                    // 传递完整视频数据到详情页，包含所有渠道的播放源信息
                    val intent = android.content.Intent(requireContext(), VideoDetailActivity::class.java)
                    intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_ID, video.vod_id.toString())
                    intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_TITLE, video.vod_name)
                    
                    // 序列化完整视频数据（包含播放源信息）
                    val videoJson = com.google.gson.Gson().toJson(video)
                    intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_DATA, videoJson)
                    
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Error opening video detail", e)
                    Toast.makeText(requireContext(), "打开详情页失败：${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            recyclerView.adapter = videoAdapter
            
            container.addView(sectionView)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "Fragment onDestroyView")
        _binding = null
    }
}
