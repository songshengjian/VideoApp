package com.example.videoapp.ui.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.R

class EpisodeAdapter(
    private val episodes: List<Episode>,
    private val currentIndex: Int,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    inner class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view_episode_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.textView.text = episode.name
        
        if (position == currentIndex) {
            holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_blue_light))
            holder.textView.background = holder.itemView.context.getDrawable(R.drawable.bg_episode_selected)
        } else {
            holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            holder.textView.background = holder.itemView.context.getDrawable(R.drawable.bg_episode_normal)
        }
        
        holder.itemView.setOnClickListener { onClick(position) }
    }

    override fun getItemCount() = episodes.size
}
