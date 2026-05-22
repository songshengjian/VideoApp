package com.example.videoapp.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SourceAdapter(
    private var sources: List<PlaySource>,
    private var currentIndex: Int,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SourceAdapter.SourceViewHolder>() {
    
    class SourceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSource: TextView = view.findViewById(R.id.textViewSource)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_source, parent, false)
        return SourceViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        val source = sources[position]
        holder.textViewSource.text = "${source.name} (${source.episodes.size}集)"
        
        // 高亮当前选中的源
        if (position == currentIndex) {
            holder.textViewSource.setTextColor(0xFFE63950.toInt())
            holder.textViewSource.setBackgroundResource(R.drawable.source_active_bg)
        } else {
            holder.textViewSource.setTextColor(0xFF666666.toInt())
            holder.textViewSource.setBackgroundResource(R.drawable.source_normal_bg)
        }
        
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }
    
    override fun getItemCount(): Int = sources.size
    
    fun updateList(newSources: List<PlaySource>, newIndex: Int) {
        sources = newSources
        currentIndex = newIndex
        notifyDataSetChanged()
    }
    
    fun updateCurrentIndex(newIndex: Int) {
        val oldIndex = currentIndex
        currentIndex = newIndex
        if (oldIndex != currentIndex) {
            notifyItemChanged(oldIndex)
            notifyItemChanged(currentIndex)
        }
    }
}