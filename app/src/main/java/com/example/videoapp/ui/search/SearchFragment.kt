package com.example.videoapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videoapp.databinding.FragmentSearchBinding
import com.example.videoapp.ui.home.VideoAdapter

class SearchFragment : Fragment() {
    
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SearchViewModel by viewModels()
    
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
        
        setupSearchView()
        setupRecyclerView()
        observeViewModel()
    }
    
    private fun setupSearchView() {
        binding.searchViewSearch.setOnQueryTextListener(object : 
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchVideos(it)
                }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
    
    private fun setupRecyclerView() {
        binding.recyclerViewSearch.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewSearch.adapter = VideoAdapter { video ->
            // 点击视频，跳转到播放页面
        }
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
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
