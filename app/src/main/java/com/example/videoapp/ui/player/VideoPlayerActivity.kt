package com.example.videoapp.ui.player

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.videoapp.databinding.ActivityVideoPlayerBinding
import kotlinx.coroutines.launch

class VideoPlayerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVideoPlayerBinding
    private var exoPlayer: ExoPlayer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL)
        val videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE)
        
        setupPlayer(videoUrl)
        binding.textViewVideoTitle.text = videoTitle
    }
    
    private fun setupPlayer(videoUrl: String?) {
        if (videoUrl.isNullOrEmpty()) {
            finish()
            return
        }
        
        exoPlayer = ExoPlayer.Builder(this).build()
        binding.playerView.player = exoPlayer
        
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true
        
        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        // 播放准备就绪
                    }
                    Player.STATE_ENDED -> {
                        // 播放结束
                    }
                    Player.STATE_BUFFERING -> {
                        // 缓冲中
                    }
                    Player.STATE_IDLE -> {
                        // 空闲状态
                    }
                }
            }
        })
    }
    
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        exoPlayer = null
    }
    
    companion object {
        const val EXTRA_VIDEO_URL = "extra_video_url"
        const val EXTRA_VIDEO_TITLE = "extra_video_title"
    }
}
