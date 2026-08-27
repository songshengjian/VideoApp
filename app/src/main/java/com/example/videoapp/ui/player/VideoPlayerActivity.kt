package com.example.videoapp.ui.player

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.R
import com.example.videoapp.data.repository.VideoRepository
import com.example.videoapp.databinding.ActivityVideoPlayerBinding
import com.google.gson.Gson
import kotlin.math.abs
import kotlinx.coroutines.launch

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

    // 广告相关
    private var bottomAdEnabled = false

    // 手势相关
    private var gestureDetector: GestureDetector? = null
    private var startGestureX = 0f
    private var startGestureY = 0f
    private var gestureMode = 0 // 0=无 1=快进 2=亮度 3=音量
    private var startBrightness = -1f
    private var startVolume = -1f
    private var lastHintText = ""
    private val gestureHintHandler = Handler(Looper.getMainLooper())
    private val hideGestureHintRunnable = Runnable { binding.textViewGestureHint.visibility = View.GONE }

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

            // 与 web 端一致：优先使用详情页传入的完整播放源列表，避免重复请求
            val passedSourcesJson = intent.getStringExtra(EXTRA_PLAY_SOURCES)
            if (!passedSourcesJson.isNullOrEmpty()) {
                try {
                    val passed = Gson().fromJson(passedSourcesJson, Array<PlaySource>::class.java)
                    if (!passed.isNullOrEmpty()) {
                        playSources = passed.toList()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "解析传入播放源失败，回退到请求详情", e)
                }
            }

            setupUI()
            setupPlayer(currentVideoUrl)

            // 加载广告配置
            loadAdsConfig()

            if (videoId.isNotEmpty()) {
                loadVideoDetail(videoId, videoTitle)
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

        // 手势：双击播放/暂停，单击切换控制栏，横滑快进，竖滑亮度/音量
        setupGestures()

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

    /**
     * 手势：双击播放/暂停、单击切换控制栏、横向拖动快进快退、竖向拖动亮度/音量
     */
    private fun setupGestures() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (isControlsVisible) {
                    hideControlBars()
                } else {
                    showControlBars()
                }
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                togglePlayPause()
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                startGestureX = e.x
                startGestureY = e.y
                gestureMode = 0
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (e1 == null) return false
                val dx = e2.x - e1.x
                val dy = e2.y - e1.y

                // 判定手势模式（仅在开始时判定一次）
                if (gestureMode == 0) {
                    if (abs(dx) > abs(dy) && abs(dx) > 30f) {
                        gestureMode = 1 // 横向快进
                    } else if (abs(dy) > abs(dx) && abs(dy) > 30f) {
                        // 竖向：左侧亮度，右侧音量
                        gestureMode = if (e1.x < binding.playerView.width / 2f) 2 else 3
                    }
                }

                when (gestureMode) {
                    1 -> handleSeekGesture(dx)
                    2 -> handleBrightnessGesture(dy)
                    3 -> handleVolumeGesture(dy)
                }
                return true
            }
        })

        binding.playerView.setOnTouchListener { _, event ->
            gestureDetector?.onTouchEvent(event)
            true
        }
    }

    private fun togglePlayPause() {
        val player = exoPlayer ?: return
        if (player.isPlaying) {
            player.pause()
            showGestureHint("⏸ 已暂停")
        } else {
            player.play()
            showGestureHint("▶ 继续播放")
        }
    }

    private fun handleSeekGesture(dx: Float) {
        val player = exoPlayer ?: return
        val duration = player.duration
        if (duration <= 0) return
        // 全宽拖动对应整个时长
        val seekDelta = (dx / binding.playerView.width) * duration
        val target = (player.currentPosition + seekDelta).toLong().coerceIn(0, duration)
        player.seekTo(target)
        showGestureHint("⏩ ${formatTime(target)} / ${formatTime(duration)}")
    }

    private fun handleBrightnessGesture(dy: Float) {
        if (startBrightness < 0) {
            startBrightness = window.attributes.screenBrightness
            if (startBrightness < 0) startBrightness = 0.5f
        }
        val delta = -dy / 800f
        val newBrightness = (startBrightness + delta).coerceIn(0.05f, 1f)
        val lp = window.attributes
        lp.screenBrightness = newBrightness
        window.attributes = lp
        showGestureHint("☀ 亮度 ${(newBrightness * 100).toInt()}%")
    }

    private fun handleVolumeGesture(dy: Float) {
        val audioManager = getSystemService(AUDIO_SERVICE) as android.media.AudioManager
        if (startVolume < 0) {
            startVolume = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC).toFloat()
        }
        val maxVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC).toFloat()
        val delta = -dy / 800f
        val newVolume = (startVolume + delta * maxVolume).coerceIn(0f, maxVolume)
        audioManager.setStreamVolume(
            android.media.AudioManager.STREAM_MUSIC,
            newVolume.toInt(),
            0
        )
        showGestureHint("🔊 音量 ${(newVolume / maxVolume * 100).toInt()}%")
    }

    private fun showGestureHint(text: String) {
        lastHintText = text
        binding.textViewGestureHint.text = text
        binding.textViewGestureHint.visibility = View.VISIBLE
        gestureHintHandler.removeCallbacks(hideGestureHintRunnable)
        gestureHintHandler.postDelayed(hideGestureHintRunnable, 1200L)
    }

    private fun formatTime(ms: Long): String {
        val totalSec = ms / 1000
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = totalSec % 60
        return if (h > 0) {
            String.format("%d:%02d:%02d", h, m, s)
        } else {
            String.format("%02d:%02d", m, s)
        }
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

    private fun loadVideoDetail(videoId: String, videoName: String = "") {
        // 详情页已传入播放源时直接使用，不再请求
        if (playSources.isNotEmpty()) {
            updateEpisodeInfo()
            return
        }

        lifecycleScope.launch {
            // 走全渠道并发详情，比单渠道接口更快且源更多
            // 传视频名称：跨渠道按名称匹配，避免各渠道 ID 不同导致源内容错配
            VideoRepository().getVideoDetailAllChannels(videoId, videoName)
                .onSuccess { video ->
                    if (video != null) {
                        playSources = if (video.play_sources.isNotEmpty()) {
                            PlaySourceParser.filterM3U8(video.play_sources.map { sourceData ->
                                PlaySource(
                                    name = "${sourceData.channel} - ${sourceData.name}",
                                    episodes = sourceData.episodes.map { ep -> Episode(ep.name, ep.url) },
                                    status = 0
                                )
                            })
                        } else {
                            PlaySourceParser.parse(video.vod_play_from, video.vod_play_url)
                        }
                        if (playSources.isNotEmpty()) {
                            updateEpisodeInfo()
                        }
                    }
                }
                .onFailure { e ->
                    Log.e(TAG, "Error loading video detail", e)
                }
        }
    }

    private fun setupPlayer(videoUrl: String) {
        try {
            // 使用 OkHttp 数据源（复用连接池，HLS 分片加载更快）；
            // 减小起播缓冲：原先 minBuffer 15s 意味着要缓冲 15s 才进 READY，弱网下起播极慢
            val dataSourceFactory = androidx.media3.datasource.okhttp.OkHttpDataSource.Factory(
                com.example.videoapp.data.api.ApiClient.playerOkHttpClient
            )
            exoPlayer = ExoPlayer.Builder(this)
                .setMediaSourceFactory(
                    androidx.media3.exoplayer.source.DefaultMediaSourceFactory(this).setDataSourceFactory(dataSourceFactory)
                )
                .setLoadControl(DefaultLoadControl.Builder()
                    .setBufferDurationsMs(5000, 50000, 1500, 5000)
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
                    // 与 web 端一致：播放失败时自动切换到下一个可用播放源
                    tryAutoSwitchSource()
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

    /**
     * 加载广告配置
     */
    private fun loadAdsConfig() {
        lifecycleScope.launch {
            try {
                val repository = VideoRepository()
                val result = repository.getAds()
                result.onSuccess { ads ->
                    // 底部广告位
                    val bottomAd = ads.video_bottom
                    if (bottomAd.enabled && bottomAd.content.isNotEmpty()) {
                        bottomAdEnabled = true
                        binding.adContainerVideoBottom.visibility = View.VISIBLE
                        binding.webviewVideoBottomAd.loadDataWithBaseURL(
                            null,
                            bottomAd.content,
                            "text/html",
                            "UTF-8",
                            null
                        )
                        Log.d(TAG, "底部广告已加载")
                    } else {
                        bottomAdEnabled = false
                        binding.adContainerVideoBottom.visibility = View.GONE
                        Log.d(TAG, "底部广告未启用或内容为空")
                    }
                }.onFailure {
                    Log.w(TAG, "广告配置加载失败：${it.message}")
                    // 默认隐藏广告位
                    binding.adContainerVideoBottom.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e(TAG, "广告配置加载异常", e)
                // 异常时隐藏广告位
                binding.adContainerVideoBottom.visibility = View.GONE
            }
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
            gestureHintHandler.removeCallbacks(hideGestureHintRunnable)
            handler.removeCallbacks(hideRunnable)
            exoPlayer?.release()
            exoPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing player", e)
        }
    }

    /**
     * 播放失败时自动尝试下一个播放源（web 端 HLS 恢复逻辑的移动端等价实现）
     */
    private fun tryAutoSwitchSource() {
        if (playSources.size <= currentSourceIndex + 1) return

        currentSourceIndex++
        currentEpisodeIndex = 0
        val source = playSources[currentSourceIndex]
        if (source.episodes.isEmpty()) {
            tryAutoSwitchSource()
            return
        }

        playVideo(source.episodes[0].url)
        updateEpisodeInfo()
        Toast.makeText(this, "当前播放源失败，已自动切换到：${source.name}", Toast.LENGTH_LONG).show()
    }

    companion object {
        const val EXTRA_VIDEO_ID = "extra_video_id"
        const val EXTRA_VIDEO_URL = "extra_video_url"
        const val EXTRA_VIDEO_TITLE = "extra_video_title"
        const val EXTRA_PLAY_SOURCES = "extra_play_sources"
    }
}
