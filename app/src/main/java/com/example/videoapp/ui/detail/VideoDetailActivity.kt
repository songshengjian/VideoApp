package com.example.videoapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videoapp.data.model.Video
import com.example.videoapp.data.repository.VideoRepository
import com.example.videoapp.databinding.ActivityVideoDetailBinding
import com.example.videoapp.ui.player.Episode
import com.example.videoapp.ui.player.PlaySource
import com.example.videoapp.ui.player.PlaySourceParser
import com.example.videoapp.ui.player.VideoPlayerActivity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    companion object {
        const val EXTRA_VIDEO_ID = "extra_video_id"
        const val EXTRA_VIDEO_TITLE = "extra_video_title"
        const val EXTRA_VIDEO_DATA = "extra_video_data"
        const val REQUEST_CODE_PLAY = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoId = intent.getStringExtra(EXTRA_VIDEO_ID) ?: ""
        videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: ""

        try {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })

            setupUI()

            // 优先使用传递的完整视频数据
            val videoDataJson = intent.getStringExtra(EXTRA_VIDEO_DATA)
            if (!videoDataJson.isNullOrEmpty()) {
                loadVideoFromSearchData(videoDataJson)
            } else {
                loadVideoDetailFromApi()
            }

            loadAdsConfig()

        } catch (e: Exception) {
            Toast.makeText(this, "错误：${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupUI() {
        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonPlay.setOnClickListener {
            startPlayback()
        }

        binding.recyclerViewSources.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewEpisodes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPopupSources.layoutManager = LinearLayoutManager(this)

        binding.buttonClosePopupSource.setOnClickListener {
            hideSourcePopup()
        }
    }

    private fun loadVideoFromSearchData(videoDataJson: String) {
        try {
            val video = Gson().fromJson(videoDataJson, Video::class.java)
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

            // 使用搜索结果中的播放源数据（与 web 端一致：仅保留 M3U8 源）
            if (video.vod_play_from.isNotEmpty() && video.vod_play_url.isNotEmpty()) {
                playSources = PlaySourceParser.parse(video.vod_play_from, video.vod_play_url).toMutableList()
                Log.d(TAG, "使用搜索数据解析播放源：${playSources.size} 个")
            }

            Toast.makeText(this, "播放源：${playSources.size}个", Toast.LENGTH_LONG).show()

            // 搜索数据中没有可用播放源时，回退到全渠道聚合详情
            if (playSources.isEmpty()) {
                loadVideoDetailFromApi()
            } else {
                setupPlaySources()
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析搜索数据失败", e)
            loadVideoDetailFromApi()
        }
    }

    private fun loadVideoDetailFromApi() {
        if (videoId.isEmpty()) {
            Toast.makeText(this, "视频 ID 为空", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            val result = VideoRepository().getVideoDetailAllChannels(videoId)
            result.onSuccess { video ->
                if (video == null) {
                    Toast.makeText(this@VideoDetailActivity, "加载视频详情失败", Toast.LENGTH_SHORT).show()
                    finish()
                    return@onSuccess
                }

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

                // 使用 play_sources 字段（与 web 端播放页一致：仅保留 M3U8 源）
                if (video.play_sources.isNotEmpty()) {
                    playSources = PlaySourceParser.filterM3U8(video.play_sources.map { sourceData ->
                        PlaySource(
                            name = "${sourceData.channel} - ${sourceData.name}",
                            episodes = sourceData.episodes.map { ep ->
                                Episode(ep.name, ep.url)
                            },
                            status = 0
                        )
                    }).toMutableList()
                    Log.d(TAG, "使用 play_sources: ${playSources.size} 个播放源")
                } else {
                    playSources = PlaySourceParser.parse(video.vod_play_from, video.vod_play_url).toMutableList()
                    Log.d(TAG, "使用 PlaySourceParser: ${playSources.size} 个播放源")
                }

                Toast.makeText(this@VideoDetailActivity, "播放源：${playSources.size}个", Toast.LENGTH_LONG).show()

                setupPlaySources()
            }.onFailure { e ->
                Log.e(TAG, "Error loading video detail", e)
                Toast.makeText(this@VideoDetailActivity, "加载详情失败：${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupPlaySources() {
        if (playSources.isNotEmpty()) {
            sourceAdapter = SourceAdapter(playSources, currentSourceIndex) { index ->
                currentSourceIndex = index
                currentEpisodeIndex = 0
                episodeAdapter?.updateList(playSources[index].episodes, 0)
                sourceAdapter?.updateCurrentIndex(index)
                Toast.makeText(this@VideoDetailActivity, "已切换到 ${playSources[index].name}", Toast.LENGTH_SHORT).show()
            }
            binding.recyclerViewSources.adapter = sourceAdapter

            episodeAdapter = EpisodeAdapter(playSources[currentSourceIndex].episodes, currentEpisodeIndex) { index ->
                currentEpisodeIndex = index
                episodeAdapter?.updateCurrentIndex(index)
            }
            binding.recyclerViewEpisodes.adapter = episodeAdapter

            binding.layoutSources.visibility = View.VISIBLE
            binding.layoutEpisodes.visibility = View.VISIBLE
            binding.buttonPlay.visibility = View.VISIBLE

            checkSourcesStatus()
        } else {
            Toast.makeText(this@VideoDetailActivity, "暂无播放资源", Toast.LENGTH_SHORT).show()
            binding.layoutSources.visibility = View.GONE
            binding.layoutEpisodes.visibility = View.GONE
            binding.buttonPlay.visibility = View.GONE
        }
    }

    private fun loadAdsConfig() {
        lifecycleScope.launch {
            try {
                val result = VideoRepository().getAds()
                result.onSuccess { config ->
                    Log.d(TAG, "广告配置加载成功：${config.video_bottom.enabled}")
                }.onFailure {
                    Log.w(TAG, "广告配置加载失败：${it.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "广告配置加载异常", e)
            }
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

    private fun checkSourcesStatus() {
        lifecycleScope.launch {
            // 并发探测所有源（原先串行逐个 HEAD 检查，弱网下会卡很久）
            val statuses = playSources.map { source ->
                async {
                    if (source.episodes.isNotEmpty()) {
                        val firstUrl = source.episodes.first().url
                        if (checkUrlValid(firstUrl)) 1 else 2
                    } else {
                        2
                    }
                }
            }.awaitAll()

            statuses.forEachIndexed { i, status ->
                playSources[i] = playSources[i].copy(status = status)
                sourceAdapter?.notifyItemChanged(i)
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

    private fun startPlayback() {
        if (videoId.isEmpty()) {
            Toast.makeText(this, "视频 ID 为空", Toast.LENGTH_SHORT).show()
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

        val intent = Intent(this, VideoPlayerActivity::class.java)
        intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_ID, videoId)
        intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_TITLE, videoTitle)
        intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, episode.url)
        // 与 web 端一致：把当前全部播放源传给播放器，播放器直接用传入数据，不再重复请求
        intent.putExtra(VideoPlayerActivity.EXTRA_PLAY_SOURCES, Gson().toJson(playSources))
        startActivityForResult(intent, REQUEST_CODE_PLAY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PLAY) {
            if (playSources.isNotEmpty()) {
                showSourcePopup()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
