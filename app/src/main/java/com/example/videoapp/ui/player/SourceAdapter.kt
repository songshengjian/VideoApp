package com.example.videoapp.ui.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.R

class SourceAdapter(
    private val sources: List<PlaySource>,
    private val currentIndex: Int,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<SourceAdapter.SourceViewHolder>() {

    inner class SourceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSource: TextView = view.findViewById(R.id.textViewSource)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_source_popup, parent, false)
        return SourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        val source = sources[position]
        holder.textViewSource.text = "${source.name} (${source.episodes.size}集)"
        
        if (position == currentIndex) {
            holder.textViewSource.setTextColor(0xFFE63950.toInt())
        } else {
            holder.textViewSource.setTextColor(0xFFFFFFFF.toInt())
        }
        
        holder.itemView.setOnClickListener { onClick(position) }
    }

    override fun getItemCount() = sources.size
}