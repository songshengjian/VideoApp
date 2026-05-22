package com.example.videoapp.ui.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.R

class EpisodeAdapter(
    private val episodes: List<Episode>,
    private val currentIndex: Int,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    inner class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewEpisode: TextView = view.findViewById(R.id.textViewEpisode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode_popup, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.textViewEpisode.text = episode.name
        
        if (position == currentIndex) {
            holder.textViewEpisode.setTextColor(0xFFE63950.toInt())
        } else {
            holder.textViewEpisode.setTextColor(0xFFFFFFFF.toInt())
        }
        
        holder.itemView.setOnClickListener { onClick(position) }
    }

    override fun getItemCount() = episodes.size
}