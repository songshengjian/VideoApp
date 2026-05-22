package com.example.videoapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoapp.R
import com.example.videoapp.data.model.Video
import com.example.videoapp.databinding.ItemVideoBinding

class VideoAdapter(
    private val onItemClick: (Video) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    
    private var videos: List<Video> = emptyList()
    
    fun submitList(newVideos: List<Video>) {
        val diffResult = DiffUtil.calculateDiff(VideoDiffCallback(videos, newVideos))
        videos = newVideos
        diffResult.dispatchUpdatesTo(this)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position], onItemClick)
    }
    
    override fun getItemCount(): Int = videos.size
    
    class VideoViewHolder(
        private val binding: ItemVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(video: Video, onItemClick: (Video) -> Unit) {
            binding.textViewVideoTitle.text = video.vod_name
            binding.textViewVideoRemarks.text = video.vod_remarks
            
            Glide.with(binding.root.context)
                .load(video.vod_pic)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(binding.imageViewVideoCover)
            
            binding.root.setOnClickListener {
                onItemClick(video)
            }
        }
    }
    
    class VideoDiffCallback(
        private val oldList: List<Video>,
        private val newList: List<Video>
    ) : DiffUtil.Callback() {
        
        override fun getOldListSize(): Int = oldList.size
        
        override fun getNewListSize(): Int = newList.size
        
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].vod_id == newList[newItemPosition].vod_id
        }
        
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
