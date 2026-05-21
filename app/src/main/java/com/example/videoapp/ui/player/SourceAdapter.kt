package com.example.videoapp.ui.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.R

class SourceAdapter(
    private val sources: List<PlaySource>,
    private val currentIndex: Int,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<SourceAdapter.SourceViewHolder>() {

    inner class SourceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view_source_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_source, parent, false)
        return SourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        val source = sources[position]
        holder.textView.text = "${source.name} (${source.episodes.size}集)"
        
        if (position == currentIndex) {
            holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_blue_light))
            holder.textView.background = holder.itemView.context.getDrawable(R.drawable.bg_source_selected)
        } else {
            holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            holder.textView.background = holder.itemView.context.getDrawable(R.drawable.bg_source_normal)
        }
        
        holder.itemView.setOnClickListener { onClick(position) }
    }

    override fun getItemCount() = sources.size
}
