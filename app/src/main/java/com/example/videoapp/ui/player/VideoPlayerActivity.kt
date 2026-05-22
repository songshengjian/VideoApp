package com.example.videoapp.ui.player

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.R
import com.example.videoapp.data.api.ApiClient
import com.example.videoapp.databinding.ActivityVideoPlayerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoPlayerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVideoPlayerBinding
    private var exoPlayer: ExoPlayer? = null
    private val TAG = "VideoPlayerActivity"
    private val handler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { hideControlBars() }
    private val HIDE_DELAY = 3000L
    
    private var videoId: String = ""
    private var currentVideoUrl: String = ""
    private var currentEpisodeIndex: Int = 0
    private var playSources: List<PlaySource> = emptyList()
    private var currentSourceIndex: Int = 0
    private var isControlsVisible = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            // 处理返回键逻辑
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        // 优先关闭弹窗
                        binding.popupSource.visibility == View.VISIBLE -> hidePopup(binding.popupSource)
                        binding.popupEpisodes.visibility == View.VISIBLE -> hidePopup(binding.popupEpisodes)
                        binding.popupSpeed.visibility == View.VISIBLE -> hidePopup(binding.popupSpeed)
                        // 然后隐藏控制栏
                        isControlsVisible -> hideControlBars()
                        // 最后确认退出
                        else -> showExitConfirmDialog()
                    }
                }
            })
            
            videoId = intent.getStringExtra(EXTRA_VIDEO_ID) ?: ""
            val videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: "未知视频"
            currentVideoUrl = intent.getStringExtra(EXTRA_VIDEO_URL) ?: ""
            
            binding.textViewVideoTitle.text = videoTitle
            
            setupUI()
            setupPlayer(currentVideoUrl)
            
            if (videoId.isNotEmpty()) {
                loadVideoDetail(videoId)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, "播放器初始化失败：${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupUI() {
        // 返回按钮（直接触发退出确认）
        binding.buttonBack.setOnClickListener {
            showExitConfirmDialog()
        }
        
        // 点击屏幕切换控制栏
        binding.playerView.setOnClickListener {
            if (isControlsVisible) {
                hideControlBars()
            } else {
                showControlBars()
            }
        }
        
        // 播放源选择
        binding.buttonSelectSource.setOnClickListener { showSourcePopup() }
        
        // 选集
        binding.buttonSelectEpisode.setOnClickListener { showEpisodePopup() }
        
        // 倍速
        binding.buttonPlaybackSpeed.setOnClickListener { showSpeedPopup() }
        
        // 关闭按钮
        binding.buttonCloseSource.setOnClickListener { hidePopup(binding.popupSource) }
        binding.buttonCloseEpisode.setOnClickListener { hidePopup(binding.popupEpisodes) }
        binding.buttonCloseSpeed.setOnClickListener { hidePopup(binding.popupSpeed) }
        
        // 倍速网格点击
        setupSpeedGrid()
    }
    
    private fun showControlBars() {
        binding.layoutTopBar.visibility = View.VISIBLE
        binding.layoutBottomBar.visibility = View.VISIBLE
        isControlsVisible = true
        handler.removeCallbacks(hideRunnable)
        handler.postDelayed(hideRunnable, HIDE_DELAY)
    }
    
    private fun hideControlBars() {
        if (binding.popupSource.visibility != View.VISIBLE &&
            binding.popupEpisodes.visibility != View.VISIBLE &&
            binding.popupSpeed.visibility != View.VISIBLE) {
            binding.layoutTopBar.visibility = View.GONE
            binding.layoutBottomBar.visibility = View.GONE
            isControlsVisible = false
        }
    }
    
    private fun showSourcePopup() {
        if (playSources.isEmpty()) {
            Toast.makeText(this, "暂无其他播放源", Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.popupSource.visibility = View.VISIBLE
        val recyclerView = binding.recyclerViewSources
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SourceAdapter(playSources, currentSourceIndex) { sourceIndex ->
            currentSourceIndex = sourceIndex
            currentEpisodeIndex = 0
            val source = playSources[sourceIndex]
            if (source.episodes.isNotEmpty()) {
                playVideo(source.episodes[0].url)
                updateEpisodeInfo()
            }
            hidePopup(binding.popupSource)
            Toast.makeText(this, "已切换到 ${source.name}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showEpisodePopup() {
        if (playSources.isEmpty() || currentSourceIndex >= playSources.size) {
            Toast.makeText(this, "暂无剧集列表", Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.popupEpisodes.visibility = View.VISIBLE
        val recyclerView = binding.recyclerViewEpisodes
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = EpisodeAdapter(playSources[currentSourceIndex].episodes, currentEpisodeIndex) { episodeIndex ->
            currentEpisodeIndex = episodeIndex
            val episode = playSources[currentSourceIndex].episodes[episodeIndex]
            playVideo(episode.url)
            updateEpisodeInfo()
            hidePopup(binding.popupEpisodes)
        }
    }
    
    private fun showSpeedPopup() {
        binding.popupSpeed.visibility = View.VISIBLE
        val currentSpeed = exoPlayer?.playbackParameters?.speed ?: 1.0f
        highlightCurrentSpeed(currentSpeed)
    }
    
    private fun highlightCurrentSpeed(speed: Float) {
        val speedMap = mapOf(
            0.5f to binding.speed05,
            0.75f to binding.speed075,
            1.0f to binding.speed10,
            1.25f to binding.speed125,
            1.5f to binding.speed15,
            2.0f to binding.speed20
        )
        
        speedMap.forEach { (s, textView) ->
            if (s == speed) {
                textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_light))
                textView.textSize = 18f
            } else {
                textView.setTextColor(getColor(android.R.color.white))
                textView.textSize = 16f
            }
        }
    }
    
    private fun setupSpeedGrid() {
        val speeds = mapOf(
            binding.speed05 to 0.5f,
            binding.speed075 to 0.75f,
            binding.speed10 to 1.0f,
            binding.speed125 to 1.25f,
            binding.speed15 to 1.5f,
            binding.speed20 to 2.0f
        )
        
        speeds.forEach { (textView, speed) ->
            textView.setOnClickListener {
                exoPlayer?.setPlaybackSpeed(speed)
                binding.buttonPlaybackSpeed.text = "${speed}x"
                Toast.makeText(this, "播放速度：${speed}x", Toast.LENGTH_SHORT).show()
                hidePopup(binding.popupSpeed)
            }
        }
    }
    
    private fun hidePopup(popup: View) {
        popup.visibility = View.GONE
        showControlBars()
    }
    
    private fun updateEpisodeInfo() {
        if (playSources.isNotEmpty() && currentSourceIndex < playSources.size) {
            val source = playSources[currentSourceIndex]
            val episodeCount = source.episodes.size
            if (currentEpisodeIndex < episodeCount) {
                val currentEpisode = source.episodes[currentEpisodeIndex]
                binding.textViewCurrentEpisode.text = "${currentEpisode.name} (${currentEpisodeIndex + 1}/$episodeCount)"
                binding.buttonSelectSource.visibility = View.VISIBLE
                binding.buttonSelectEpisode.visibility = View.VISIBLE
            }
        }
    }
    
    private fun loadVideoDetail(videoId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.videoApi.getVideoDetail(videoId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val video = response.body()!!.list.firstOrNull()
                        if (video != null) {
                            playSources = parsePlaySources(video.vod_play_from, video.vod_play_url)
                            if (playSources.isNotEmpty()) {
                                updateEpisodeInfo()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading video detail", e)
            }
        }
    }
    
    private fun parsePlaySources(playFrom: String, playUrl: String): List<PlaySource> {
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
    
    private fun setupPlayer(videoUrl: String) {
        try {
            exoPlayer = ExoPlayer.Builder(this)
                .setLoadControl(DefaultLoadControl.Builder()
                    .setBufferDurationsMs(15000, 50000, 3000, 3000)
                    .build()
                )
                .build()
                
            binding.playerView.player = exoPlayer
            
            exoPlayer?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            Log.d(TAG, "Playback ready")
                            showControlBars()
                            updateEpisodeInfo()
                        }
                        Player.STATE_ENDED -> playNextEpisode()
                        Player.STATE_BUFFERING -> Log.d(TAG, "Buffering")
                        Player.STATE_IDLE -> Log.d(TAG, "Idle")
                    }
                }
                
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    Log.e(TAG, "Player error code: ${error.errorCode}, message: ${error.message}", error)
                    val errorMsg = when (error.errorCode) {
                        androidx.media3.common.PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> "网络连接失败，请检查网络"
                        androidx.media3.common.PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> "不支持的视频格式，请尝试切换播放源"
                        androidx.media3.common.PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> "视频解码失败，请尝试切换播放源"
                        else -> "播放失败：${error.message ?: "未知错误"}"
                    }
                    Toast.makeText(this@VideoPlayerActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            })
            
            playVideo(videoUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up player", e)
            Toast.makeText(this, "播放器设置失败：${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun playVideo(videoUrl: String) {
        try {
            currentVideoUrl = videoUrl
            val url = videoUrl.trim()
            
            if (url.isEmpty()) {
                Toast.makeText(this, "视频链接为空", Toast.LENGTH_SHORT).show()
                return
            }
            
            Log.d(TAG, "Playing video URL: $url")
            
            val uri = Uri.parse(url)
            val mediaItemBuilder = MediaItem.Builder()
                .setUri(uri)
            
            exoPlayer?.setMediaItem(mediaItemBuilder.build())
            exoPlayer?.prepare()
            exoPlayer?.playWhenReady = true
            
            Log.d(TAG, "Video playback started")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing video: ${e.message}", e)
            Toast.makeText(this, "播放失败：${e.message ?: "未知错误"}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun playNextEpisode() {
        if (playSources.isNotEmpty() && currentSourceIndex < playSources.size) {
            val source = playSources[currentSourceIndex]
            if (currentEpisodeIndex < source.episodes.size - 1) {
                currentEpisodeIndex++
                val nextEpisode = source.episodes[currentEpisodeIndex]
                playVideo(nextEpisode.url)
                updateEpisodeInfo()
                Toast.makeText(this, "正在播放：${nextEpisode.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showExitConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("返回详情")
            .setMessage("是否返回视频详情页？")
            .setPositiveButton("返回") { _, _ -> 
                setResult(RESULT_OK)
                finish()
            }
            .setNegativeButton("继续观看", null)
            .show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            exoPlayer?.release()
            exoPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing player", e)
        }
    }
    
    companion object {
        const val EXTRA_VIDEO_ID = "extra_video_id"
        const val EXTRA_VIDEO_URL = "extra_video_url"
        const val EXTRA_VIDEO_TITLE = "extra_video_title"
    }
}
