package com.example.videoapp.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.R
import com.example.videoapp.ui.player.PlaySource

class SourceAdapter(
    private var sources: List<PlaySource>,
    private var currentIndex: Int,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SourceAdapter.SourceViewHolder>() {
    
    class SourceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSource: TextView = view.findViewById(R.id.textViewSource)
        val textViewStatus: TextView = view.findViewById(R.id.textViewStatus)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_source, parent, false)
        return SourceViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        val source = sources[position]
        holder.textViewSource.text = "${source.name} (${source.episodes.size}集)"
        
        // 显示状态
        val statusText = when (source.status) {
            1 -> "成功"
            2 -> "失败"
            else -> "待检测"
        }
        holder.textViewStatus.text = statusText
        holder.textViewStatus.setTextColor(
            when (source.status) {
                1 -> 0xFF4CAF50.toInt()  // 绿色-成功
                2 -> 0xFFF44336.toInt()   // 红色-失败
                else -> 0xFF999999.toInt() // 灰色-待检测
            }
        )
        
        // 根据剧集数量设置样式
        val isEmpty = source.episodes.isEmpty()
        
        // 高亮当前选中的源
        if (position == currentIndex) {
            holder.textViewSource.setTextColor(0xFFFFFFFF.toInt())  // 白色文字
            holder.textViewSource.setBackgroundResource(R.drawable.source_active_bg)
        } else if (isEmpty) {
            holder.textViewSource.setTextColor(0xFF999999.toInt())
            holder.textViewSource.setBackgroundResource(R.drawable.source_normal_bg)
        } else {
            holder.textViewSource.setTextColor(0xFF333333.toInt())
            holder.textViewSource.setBackgroundResource(R.drawable.source_normal_bg)
        }
        
        holder.itemView.isEnabled = !isEmpty
        holder.itemView.setOnClickListener {
            if (!isEmpty) {
                onItemClick(position)
            }
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