package com.example.videoapp.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.data.model.Category
import com.example.videoapp.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    
    private var categories: List<Category> = emptyList()
    
    fun submitList(newCategories: List<Category>) {
        val diffResult = DiffUtil.calculateDiff(CategoryDiffCallback(categories, newCategories))
        categories = newCategories
        diffResult.dispatchUpdatesTo(this)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], onItemClick)
    }
    
    override fun getItemCount(): Int = categories.size
    
    class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(category: Category, onItemClick: (Category) -> Unit) {
            binding.textViewCategoryName.text = category.type_name
            
            binding.root.setOnClickListener {
                onItemClick(category)
            }
        }
    }
    
    class CategoryDiffCallback(
        private val oldList: List<Category>,
        private val newList: List<Category>
    ) : DiffUtil.Callback() {
        
        override fun getOldListSize(): Int = oldList.size
        
        override fun getNewListSize(): Int = newList.size
        
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].type_id == newList[newItemPosition].type_id
        }
        
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
