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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.R
import com.example.videoapp.data.api.ApiClient
import com.example.videoapp.data.model.Video
import com.example.videoapp.databinding.ActivityVideoDetailBinding
import com.example.videoapp.ui.player.VideoPlayerActivity
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
    
    private var sourceAdapter: SourceAdapter? = null
    private var episodeAdapter: EpisodeAdapter? = null
    private var popupSourceAdapter: SourceAdapter? = null
    
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
        
        // 设置播放源列表（纵向）
        binding.recyclerViewSources.layoutManager = LinearLayoutManager(this)
        
        // 设置集列表
        binding.recyclerViewEpisodes.layoutManager = LinearLayoutManager(this)
        
        // 弹窗中的播放源列表
        binding.recyclerViewPopupSources.layoutManager = LinearLayoutManager(this)
        
        // 关闭弹窗按钮
        binding.buttonClosePopupSource.setOnClickListener {
            hideSourcePopup()
        }
    }
    
    private fun showSourcePopup() {
        binding.popupSource.visibility = View.VISIBLE
        if (popupSourceAdapter == null && playSources.isNotEmpty()) {
            popupSourceAdapter = SourceAdapter(playSources, currentSourceIndex) { index ->
                val source = playSources[index]
                if (source.episodes.isEmpty()) {
                    Toast.makeText(this, "该渠道暂无剧集", Toast.LENGTH_SHORT).show()
                    return@SourceAdapter
                }
                currentSourceIndex = index
                currentEpisodeIndex = 0
                episodeAdapter?.updateList(playSources[index].episodes, 0)
                sourceAdapter?.updateCurrentIndex(index)
                popupSourceAdapter?.updateCurrentIndex(index)
                hideSourcePopup()
                // 自动开始播放
                startPlayback()
                Toast.makeText(this, "已切换到 ${source.name}", Toast.LENGTH_SHORT).show()
            }
            binding.recyclerViewPopupSources.adapter = popupSourceAdapter
        } else {
            popupSourceAdapter?.updateCurrentIndex(currentSourceIndex)
        }
    }
    
    private fun hideSourcePopup() {
        binding.popupSource.visibility = View.GONE
    }
    
    private fun loadVideoDetail() {
        if (videoId.isEmpty()) {
            Toast.makeText(this, "视频ID为空", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        lifecycleScope.launch {
            try {
                // 使用搜索接口获取所有渠道的视频数据
                val response = ApiClient.videoApi.searchVideosForDetail(videoTitle)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val videoList = response.body()!!.list
                        // 找到匹配的视频（通过ID或名称）
                        val video = videoList.find { it.vod_id.toString() == videoId } 
                            ?: videoList.firstOrNull()
                        
                        if (video != null) {
                            currentVideo = video
                            binding.textViewVideoTitle.text = video.vod_name
                            binding.textViewAppBarTitle.text = video.vod_name
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
                                // 创建播放源适配器
                                sourceAdapter = SourceAdapter(playSources, currentSourceIndex) { index ->
                                    currentSourceIndex = index
                                    currentEpisodeIndex = 0
                                    episodeAdapter?.updateList(playSources[index].episodes, 0)
                                    sourceAdapter?.updateCurrentIndex(index)
                                    Toast.makeText(this@VideoDetailActivity, "已切换到 ${playSources[index].name}", Toast.LENGTH_SHORT).show()
                                }
                                binding.recyclerViewSources.adapter = sourceAdapter
                                
                                // 创建集数适配器
                                episodeAdapter = EpisodeAdapter(playSources[currentSourceIndex].episodes, currentEpisodeIndex) { index ->
                                    currentEpisodeIndex = index
                                    episodeAdapter?.updateCurrentIndex(index)
                                }
                                binding.recyclerViewEpisodes.adapter = episodeAdapter
                                
                                // 直接显示播放源和选集列表（不弹窗）
                                binding.layoutSources.visibility = View.VISIBLE
                                binding.layoutEpisodes.visibility = View.VISIBLE
                                binding.buttonPlay.visibility = View.VISIBLE
                                
                                // 检测所有播放源状态（异步）
                                checkSourcesStatus()
                            } else {
                                Toast.makeText(this@VideoDetailActivity, "暂无播放资源", Toast.LENGTH_SHORT).show()
                                binding.layoutSources.visibility = View.GONE
                                binding.layoutEpisodes.visibility = View.GONE
                                binding.buttonPlay.visibility = View.GONE
                            }
                        } else {
                            Toast.makeText(this@VideoDetailActivity, "视频信息不存在", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this@VideoDetailActivity, "加载视频详情失败", Toast.LENGTH_SHORT).show()
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
        
        val sourceNames = playFrom.split("$$$").map { it.trim() }
        val sourceUrls = playUrl.split("$$$").map { it.trim() }
        
        val count = minOf(sourceNames.size, sourceUrls.size)
        for (i in 0 until count) {
            val name = sourceNames[i]
            val urlStr = sourceUrls[i]
            
            if (name.isEmpty()) continue
            
            val episodes = mutableListOf<Episode>()
            
            if (urlStr.isNotEmpty()) {
                val episodeParts = urlStr.split("#").filter { it.isNotEmpty() }
                for (part in episodeParts) {
                    val episodeData = part.split("$")
                    if (episodeData.size >= 2) {
                        episodes.add(Episode(episodeData[0].trim(), episodeData[1].trim()))
                    }
                }
            }
            
            sources.add(PlaySource(name, episodes, 0))
        }
        
        return sources
    }
    
    private fun updateSourceList() {
        sourceAdapter?.notifyDataSetChanged()
    }
    
    private fun checkSourcesStatus() {
        lifecycleScope.launch {
            for (i in playSources.indices) {
                val source = playSources[i]
                if (source.episodes.isNotEmpty()) {
                    val firstUrl = source.episodes.first().url
                    val isValid = checkUrlValid(firstUrl)
                    playSources[i] = source.copy(status = if (isValid) 1 else 2)
                } else {
                    playSources[i] = source.copy(status = 2)
                }
                withContext(Dispatchers.Main) {
                    sourceAdapter?.notifyItemChanged(i)
                }
            }
        }
    }
    
    private suspend fun checkUrlValid(url: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val responseCode = connection.responseCode
            connection.disconnect()
            responseCode in 200..399
        } catch (e: Exception) {
            false
        }
    }
    
    private fun updateEpisodeList() {
        if (currentSourceIndex < playSources.size) {
            val episodes = playSources[currentSourceIndex].episodes
            episodeAdapter?.updateList(episodes, currentEpisodeIndex)
        }
    }
    
    private fun startPlayback() {
        if (videoId.isEmpty()) {
            Toast.makeText(this, "视频ID为空", Toast.LENGTH_SHORT).show()
            return
        }
        
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
            // 从播放器返回时，显示播放源选择弹窗
            if (playSources.isNotEmpty()) {
                showSourcePopup()
            }
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
    val episodes: List<Episode>,
    var status: Int = 0  // 0=待检测, 1=成功, 2=失败
)

data class Episode(
    val name: String,
    val url: String
)