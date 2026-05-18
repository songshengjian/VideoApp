package com.example.videoapp.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videoapp.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {
    
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CategoriesViewModel by viewModels()
    
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
        
        setupRecyclerView()
        observeViewModel()
        viewModel.loadCategories()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCategories.adapter = CategoryAdapter { category ->
            // 点击分类，跳转到分类视频列表
        }
    }
    
    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            categories?.let {
                (binding.recyclerViewCategories.adapter as? CategoryAdapter)?.submitList(it)
            }
        })
        
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBarCategories.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
