package com.example.videoapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videoapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
        viewModel.loadHomeVideos()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerViewHome.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewHome.adapter = VideoAdapter { video ->
            // 点击视频，跳转到播放页面
        }
    }
    
    private fun observeViewModel() {
        viewModel.videos.observe(viewLifecycleOwner, Observer { videos ->
            videos?.let {
                (binding.recyclerViewHome.adapter as? VideoAdapter)?.submitList(it)
            }
        })
        
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBarHome.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
        
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                // 显示错误提示
            }
        })
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
