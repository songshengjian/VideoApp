package com.example.videoapp.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.R
import com.example.videoapp.ui.player.Episode

class EpisodeAdapter(
    private var episodes: List<Episode>,
    private var currentIndex: Int,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {
    
    class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewEpisode: TextView = view.findViewById(R.id.textViewEpisode)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.textViewEpisode.text = episode.name
        
        // 高亮当前选中的集
        if (position == currentIndex) {
            holder.textViewEpisode.setTextColor(0xFFFFFFFF.toInt())
            holder.textViewEpisode.setBackgroundResource(R.drawable.episode_active_bg)
        } else {
            holder.textViewEpisode.setTextColor(0xFF666666.toInt())
            holder.textViewEpisode.setBackgroundResource(R.drawable.episode_normal_bg)
        }
        
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }
    
    override fun getItemCount(): Int = episodes.size
    
    fun updateList(newEpisodes: List<Episode>, newIndex: Int) {
        episodes = newEpisodes
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