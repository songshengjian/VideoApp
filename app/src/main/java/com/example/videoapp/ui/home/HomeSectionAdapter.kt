package com.example.videoapp.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoapp.R
import com.example.videoapp.data.model.Video
import com.example.videoapp.databinding.ItemHomeSectionBinding
import com.example.videoapp.databinding.ItemHomeVideoBinding
import com.example.videoapp.ui.detail.VideoDetailActivity

class HomeSectionAdapter(
    private val onVideoClick: (Video) -> Unit
) : RecyclerView.Adapter<HomeSectionAdapter.SectionViewHolder>() {
    
    private var sections: List<HomeSection> = emptyList()
    
    fun setSections(newSections: List<HomeSection>) {
        sections = newSections
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val binding = ItemHomeSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SectionViewHolder(binding, parent.context)
    }
    
    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(sections[position])
    }
    
    override fun getItemCount(): Int = sections.size
    
    inner class SectionViewHolder(
        private val binding: ItemHomeSectionBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(section: HomeSection) {
            binding.textViewSectionTitle.text = section.title
            
            binding.recyclerViewSectionVideos.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            
            binding.recyclerViewSectionVideos.adapter = SectionVideoAdapter(section.videos) { video ->
                onVideoClick(video)
            }
        }
    }
    
    inner class SectionVideoAdapter(
        private val videos: List<Video>,
        private val onItemClick: (Video) -> Unit
    ) : RecyclerView.Adapter<SectionVideoAdapter.VideoViewHolder>() {
        
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
}
