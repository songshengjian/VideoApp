package com.example.videoapp.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoapp.R
import com.example.videoapp.data.model.Video
import com.example.videoapp.databinding.ItemHomeVideoBinding
import com.example.videoapp.ui.player.VideoPlayerActivity

class HomeSectionVideoAdapter(
    private val videos: List<Video>,
    private val onItemClick: (Video) -> Unit
) : RecyclerView.Adapter<HomeSectionVideoAdapter.VideoViewHolder>() {
    
    private val TAG = "SectionVideoAdapter"
    
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
                val playUrl = parseFirstEpisodeUrl(video.vod_play_url)
                if (playUrl.isEmpty()) {
                    Toast.makeText(context, "该视频暂无播放资源", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
                val intent = android.content.Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_ID, video.vod_id.toString())
                intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_TITLE, video.vod_name)
                intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, playUrl)
                context.startActivity(intent)
            }
        }
        
        private fun parseFirstEpisodeUrl(playUrl: String): String {
            if (playUrl.isEmpty()) return ""
            try {
                val episodes = playUrl.split("#")
                if (episodes.isNotEmpty()) {
                    val firstEpisode = episodes[0]
                    val parts = firstEpisode.split("$")
                    if (parts.size >= 2) {
                        return parts[1]
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing play URL", e)
            }
            return ""
        }
    }
}
