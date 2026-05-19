package com.example.videoapp.ui.player

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
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
    
    private var videoId: String = ""
    private var currentVideoUrl: String = ""
    private var currentEpisodeIndex: Int = 0
    private var playSources: List<PlaySource> = emptyList()
    private var currentSourceIndex: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "Activity onCreate")
            binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            videoId = intent.getStringExtra(EXTRA_VIDEO_ID) ?: ""
            val videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: "未知视频"
            currentVideoUrl = intent.getStringExtra(EXTRA_VIDEO_URL) ?: ""
            
            Log.d(TAG, "Received video ID: $videoId")
            Log.d(TAG, "Received video title: $videoTitle")
            Log.d(TAG, "Received video URL: ${currentVideoUrl.take(100)}...")
            
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
        binding.buttonSelectSource.setOnClickListener { showSourceSelector() }
        binding.buttonSelectEpisode.setOnClickListener { showEpisodeSelector() }
        binding.buttonPlaybackSpeed.setOnClickListener { showPlaybackSpeedSelector() }
    }
    
    private fun showPlaybackSpeedSelector() {
        val speeds = arrayOf("0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "1.75x", "2.0x")
        val currentSpeed = exoPlayer?.playbackParameters?.speed ?: 1.0f
        val currentIndex = speeds.indexOf("${currentSpeed}x")
            .takeIf { it >= 0 } ?: speeds.indexOf("1.0x")
        
        AlertDialog.Builder(this)
            .setTitle("播放速度")
            .setSingleChoiceItems(speeds, currentIndex) { dialog, which ->
                val newSpeed = when (which) {
                    0 -> 0.5f
                    1 -> 0.75f
                    2 -> 1.0f
                    3 -> 1.25f
                    4 -> 1.5f
                    5 -> 1.75f
                    6 -> 2.0f
                    else -> 1.0f
                }
                exoPlayer?.setPlaybackSpeed(newSpeed)
                binding.buttonPlaybackSpeed.text = "${newSpeed}x"
                Toast.makeText(this, "播放速度：${newSpeed}x", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showSourceSelector() {
        if (playSources.isEmpty()) {
            Toast.makeText(this, "暂无其他播放源", Toast.LENGTH_SHORT).show()
            return
        }
        
        val sourceNames = playSources.map { it.name }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("选择播放源")
            .setItems(sourceNames) { dialog, which ->
                currentSourceIndex = which
                currentEpisodeIndex = 0
                val source = playSources[which]
                if (source.episodes.isNotEmpty()) {
                    playVideo(source.episodes[0].url)
                    updateInfoText()
                    Toast.makeText(this, "已切换到 ${source.name}", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showEpisodeSelector() {
        if (playSources.isEmpty() || currentSourceIndex >= playSources.size) {
            Toast.makeText(this, "暂无剧集列表", Toast.LENGTH_SHORT).show()
            return
        }
        
        val source = playSources[currentSourceIndex]
        val episodeNames = source.episodes.map { it.name }.toTypedArray()
        
        AlertDialog.Builder(this)
            .setTitle("选择剧集 - ${source.name}")
            .setItems(episodeNames) { dialog, which ->
                currentEpisodeIndex = which
                val episode = source.episodes[which]
                playVideo(episode.url)
                updateInfoText()
                Toast.makeText(this, "播放：${episode.name}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun updateInfoText() {
        if (playSources.isNotEmpty() && currentSourceIndex < playSources.size) {
            val source = playSources[currentSourceIndex]
            val episodeCount = source.episodes.size
            val currentEpisode = if (currentEpisodeIndex < episodeCount) {
                source.episodes[currentEpisodeIndex].name
            } else {
                "未知"
            }
            binding.textViewCurrentInfo.text = "${source.name} - $currentEpisode (${currentEpisodeIndex + 1}/$episodeCount)"
            binding.textViewCurrentInfo.visibility = View.VISIBLE
            binding.buttonSelectSource.visibility = View.VISIBLE
            binding.buttonSelectEpisode.visibility = View.VISIBLE
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
                                updateInfoText()
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
            playVideo(videoUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up player", e)
            Toast.makeText(this, "播放器设置失败：${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun playVideo(videoUrl: String) {
        try {
            currentVideoUrl = videoUrl
            val uri = Uri.parse(videoUrl)
            val mediaItem = MediaItem.fromUri(uri)
            
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.playWhenReady = true
            
            exoPlayer?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            Log.d(TAG, "Playback ready")
                            // 显示控制按钮
                            binding.buttonSelectSource.visibility = View.VISIBLE
                            binding.buttonSelectEpisode.visibility = View.VISIBLE
                            binding.buttonPlaybackSpeed.visibility = View.VISIBLE
                            updateInfoText()
                        }
                        Player.STATE_ENDED -> playNextEpisode()
                        Player.STATE_BUFFERING -> Log.d(TAG, "Buffering")
                        Player.STATE_IDLE -> Log.d(TAG, "Idle")
                    }
                }
                
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    Log.e(TAG, "Player error: ${error.errorCodeName}", error)
                    val errorMsg = when (error.errorCode) {
                        androidx.media3.common.PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> "网络连接失败，请检查网络"
                        androidx.media3.common.PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> "不支持的视频格式"
                        else -> "播放失败：${error.message}"
                    }
                    Toast.makeText(this@VideoPlayerActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error playing video", e)
            Toast.makeText(this, "播放失败：${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun playNextEpisode() {
        if (playSources.isNotEmpty() && currentSourceIndex < playSources.size) {
            val source = playSources[currentSourceIndex]
            if (currentEpisodeIndex < source.episodes.size - 1) {
                currentEpisodeIndex++
                val nextEpisode = source.episodes[currentEpisodeIndex]
                playVideo(nextEpisode.url)
                updateInfoText()
                Toast.makeText(this, "正在播放：${nextEpisode.name}", Toast.LENGTH_SHORT).show()
            }
        }
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
