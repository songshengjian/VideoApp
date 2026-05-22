package com.example.videoapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.R
import com.example.videoapp.data.api.ApiClient
import com.example.videoapp.data.model.Video
import com.example.videoapp.databinding.ActivityVideoDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVideoDetailBinding
    private val TAG = "VideoDetailActivity"
    
    private var videoId: String = ""
    private var videoTitle: String = ""
    private var currentVideo: Video? = null
    
    private var playSources: MutableList<PlaySource> = mutableListOf()
    private var currentSourceIndex: Int = 0
    private var currentEpisodeIndex: Int = 0
    
    private lateinit var sourceAdapter: SourceAdapter
    private lateinit var episodeAdapter: EpisodeAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityVideoDetailBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            // 处理返回键
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
            
            videoId = intent.getStringExtra(EXTRA_VIDEO_ID) ?: ""
            videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: ""
            
            binding.textViewAppBarTitle.text = videoTitle
            
            setupUI()
            
            if (videoId.isNotEmpty()) {
                loadVideoDetail()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, "详情页初始化失败：${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupUI() {
        // 返回按钮
        binding.buttonBack.setOnClickListener {
            finish()
        }
        
        // 播放按钮
        binding.buttonPlay.setOnClickListener {
            startPlayback()
        }
        
        // 设置播放源列表
        sourceAdapter = SourceAdapter(playSources, currentSourceIndex) { index ->
            currentSourceIndex = index
            currentEpisodeIndex = 0
            updateEpisodeList()
            Toast.makeText(this, "已切换到 ${playSources[index].name}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewSources.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewSources.adapter = sourceAdapter
        
        // 设置集列表
        episodeAdapter = EpisodeAdapter(mutableListOf(), currentEpisodeIndex) { index ->
            currentEpisodeIndex = index
            updateEpisodeList()
        }
        binding.recyclerViewEpisodes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewEpisodes.adapter = episodeAdapter
    }
    
    private fun loadVideoDetail() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.videoApi.getVideoDetail(videoId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val video = response.body()!!.list.firstOrNull()
                        if (video != null) {
                            currentVideo = video
                            binding.textViewVideoTitle.text = video.vod_name
                            binding.textViewVideoYear.text = "年份：${video.vod_year ?: "未知"}"
                            binding.textViewVideoArea.text = "地区：${video.vod_area ?: "未知"}"
                            binding.textViewVideoType.text = "类型：${video.vod_class ?: "未知"}"
                            
                            var content = video.vod_content ?: "暂无简介"
                            if (content.length > 200) {
                                content = content.substring(0, 200) + "..."
                            }
                            binding.textViewVideoDesc.text = "简介：$content"
                            
                            playSources = parsePlaySources(video.vod_play_from, video.vod_play_url)
                            if (playSources.isNotEmpty()) {
                                updateSourceList()
                                updateEpisodeList()
                                binding.layoutSources.visibility = View.VISIBLE
                                binding.layoutEpisodes.visibility = View.VISIBLE
                                binding.buttonPlay.visibility = View.VISIBLE
                            } else {
                                Toast.makeText(this@VideoDetailActivity, "暂无播放资源", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading video detail", e)
                Toast.makeText(this@VideoDetailActivity, "加载详情失败：${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun parsePlaySources(playFrom: String, playUrl: String): MutableList<PlaySource> {
        val sources = mutableListOf<PlaySource>()
        if (playFrom.isEmpty() || playUrl.isEmpty()) return sources
        
        val sourceNames = playFrom.split("$$$").map { it.trim() }.filter { it.isNotEmpty() }
        val sourceUrls = playUrl.split("$$$").map { it.trim() }.filter { it.isNotEmpty() }
        
        val count = minOf(sourceNames.size, sourceUrls.size)
        for (i in 0 until count) {
            val name = sourceNames[i]
            val urlStr = sourceUrls[i]
            val episodes = mutableListOf<Episode>()
            
            val episodeParts = urlStr.split("#").filter { it.isNotEmpty() }
            for (part in episodeParts) {
                val episodeData = part.split("$").filter { it.isNotEmpty() }
                if (episodeData.size >= 2) {
                    episodes.add(Episode(episodeData[0].trim(), episodeData[1].trim()))
                }
            }
            
            if (episodes.isNotEmpty()) {
                sources.add(PlaySource(name, episodes))
            }
        }
        
        return sources
    }
    
    private fun updateSourceList() {
        sourceAdapter.notifyDataSetChanged()
    }
    
    private fun updateEpisodeList() {
        if (currentSourceIndex < playSources.size) {
            val episodes = playSources[currentSourceIndex].episodes
            episodeAdapter.updateList(episodes, currentEpisodeIndex)
        }
    }
    
    private fun startPlayback() {
        if (playSources.isEmpty() || currentSourceIndex >= playSources.size) {
            Toast.makeText(this, "暂无播放资源", Toast.LENGTH_SHORT).show()
            return
        }
        
        val source = playSources[currentSourceIndex]
        if (currentEpisodeIndex >= source.episodes.size) {
            Toast.makeText(this, "暂无剧集", Toast.LENGTH_SHORT).show()
            return
        }
        
        val episode = source.episodes[currentEpisodeIndex]
        
        val intent = Intent(this, com.example.videoapp.ui.player.VideoPlayerActivity::class.java)
        intent.putExtra(com.example.videoapp.ui.player.VideoPlayerActivity.EXTRA_VIDEO_ID, videoId)
        intent.putExtra(com.example.videoapp.ui.player.VideoPlayerActivity.EXTRA_VIDEO_TITLE, videoTitle)
        intent.putExtra(com.example.videoapp.ui.player.VideoPlayerActivity.EXTRA_VIDEO_URL, episode.url)
        startActivityForResult(intent, REQUEST_CODE_PLAY)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PLAY) {
            // 播放器返回，保持在详情页，可以选择其他剧集或切换播放源
            // 不需要做任何处理
        }
    }
    
    // 从播放器返回时，可能更新选集状态
    override fun onResume() {
        super.onResume()
        // 如果需要从播放器更新当前播放进度，可以在这里处理
    }
    
    companion object {
        const val EXTRA_VIDEO_ID = "extra_video_id"
        const val EXTRA_VIDEO_TITLE = "extra_video_title"
        const val REQUEST_CODE_PLAY = 1001
    }
}

data class PlaySource(
    val name: String,
    val episodes: List<Episode>
)

data class Episode(
    val name: String,
    val url: String
)