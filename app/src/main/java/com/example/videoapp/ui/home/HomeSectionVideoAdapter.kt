package com.example.videoapp.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoapp.R
import com.example.videoapp.data.model.Video
import com.example.videoapp.databinding.ItemHomeVideoBinding
import com.example.videoapp.ui.detail.VideoDetailActivity

class HomeSectionVideoAdapter(
    private val videos: List<Video>,
    private val onItemClick: (Video) -> Unit
) : RecyclerView.Adapter<HomeSectionVideoAdapter.VideoViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemHomeVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding, parent.context)
    }
    
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }
    
    override fun getItemCount(): Int = videos.size
    
    inner class VideoViewHolder(
        private val binding: ItemHomeVideoBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(video: Video) {
            binding.textViewVideoTitle.text = video.vod_name
            binding.textViewVideoRemarks.text = video.vod_remarks
            
            Glide.with(binding.root.context)
                .load(video.vod_pic)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(binding.imageViewVideoCover)
            
            binding.root.setOnClickListener {
                // 跳转到视频详情页
                val intent = android.content.Intent(context, VideoDetailActivity::class.java)
                intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_ID, video.vod_id.toString())
                intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_TITLE, video.vod_name)
                context.startActivity(intent)
            }
        }
    }
}
