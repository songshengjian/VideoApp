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
import com.example.videoapp.data.api.ApiClient
import com.example.videoapp.data.repository.VideoRepository
import com.example.videoapp.databinding.ActivityVideoDetailBinding
import com.example.videoapp.ui.player.Episode
import com.example.videoapp.ui.player.PlaySource
import com.example.videoapp.ui.player.VideoPlayerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVideoDetailBinding
    private val TAG = "VideoDetailActivity"
    
    private var videoId: String = ""
    private var videoTitle: String = ""
    private var currentVideo: com.example.videoapp.data.model.Video? = null
    
    private var playSources: MutableList<PlaySource> = mutableListOf()
    private var currentSourceIndex: Int = 0
    private var currentEpisodeIndex: Int = 0
    
    private var sourceAdapter: SourceAdapter? = null
    private var episodeAdapter: EpisodeAdapter? = null
    private var popupSourceAdapter: SourceAdapter? = null
    
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
            loadVideoDetail()
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
    
    private fun loadVideoDetail() {
        if (videoId.isEmpty()) {
            Toast.makeText(this, "视频 ID 为空", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // 优先使用搜索传递的完整视频数据
        val videoDataJson = intent.getStringExtra(EXTRA_VIDEO_DATA)
        if (!videoDataJson.isNullOrEmpty()) {
            try {
                val video = com.google.gson.Gson().fromJson(videoDataJson, com.example.videoapp.data.model.Video::class.java)
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
                
                // 使用搜索结果中的播放源数据（包含所有渠道）
                // 注意：搜索结果中的 play_sources 可能为空，需要解析 vod_play_from
                playSources = parsePlaySources(video.vod_play_from, video.vod_play_url)
                Log.d(TAG, "使用搜索数据解析播放源：${playSources.size} 个")
                
                Toast.makeText(this, "播放源：${playSources.size}个", Toast.LENGTH_LONG).show()
                
                setupPlaySources()
                return
            } catch (e: Exception) {
                Log.e(TAG, "解析搜索数据失败", e)
            }
        }
        
        // 没有搜索数据时，调用 API 获取
        lifecycleScope.launch {
            try {
                val response = ApiClient.videoApi.getVideoDetailAllChannels(videoId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        
                        if (body.code == 1 && body.list.isNotEmpty()) {
                            val video = body.list.first()
                            
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
                            
                            // 直接使用 play_sources（后端已聚合的渠道数据）
                            if (video.play_sources.isNotEmpty()) {
                                playSources = video.play_sources.map { sourceData ->
                                    PlaySource(
                                        name = "${sourceData.channel} - ${sourceData.name}",
                                        episodes = sourceData.episodes.map { ep ->
                                            Episode(ep.name, ep.url)
                                        },
                                        status = 0
                                    )
                                }.toMutableList()
                                Log.d(TAG, "使用 play_sources: ${playSources.size} 个播放源")
                            } else {
                                playSources = parsePlaySources(video.vod_play_from, video.vod_play_url)
                                Log.d(TAG, "使用 parsePlaySources: ${playSources.size} 个播放源")
                            }
                            
                            Toast.makeText(this@VideoDetailActivity, "播放源：${playSources.size}个", Toast.LENGTH_LONG).show()
                            
                            setupPlaySources()
                        } else {
                            Toast.makeText(this@VideoDetailActivity, body.msg, Toast.LENGTH_SHORT).show()
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
        
        lifecycleScope.launch {
            try {
                val response = ApiClient.videoApi.getVideoDetailAllChannels(videoId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        
                        if (body.code == 1 && body.list.isNotEmpty()) {
                            val video = body.list.first()
                            
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
                            
                            // 直接使用 play_sources（后端已聚合所有渠道）
                            if (video.play_sources.isNotEmpty()) {
                                playSources = video.play_sources.map { sourceData ->
                                    PlaySource(
                                        name = "${sourceData.channel} - ${sourceData.name}",
                                        episodes = sourceData.episodes.map { ep ->
                                            Episode(ep.name, ep.url)
                                        },
                                        status = 0
                                    )
                                }.toMutableList()
                                Log.d(TAG, "使用 play_sources: ${playSources.size} 个播放源")
                            } else {
                                playSources = parsePlaySources(video.vod_play_from, video.vod_play_url)
                                Log.d(TAG, "使用 parsePlaySources: ${playSources.size} 个播放源")
                            }
                            
                            Toast.makeText(this@VideoDetailActivity, "播放源：${playSources.size}个", Toast.LENGTH_LONG).show()
                            
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
                        } else {
                            Toast.makeText(this@VideoDetailActivity, body.msg, Toast.LENGTH_SHORT).show()
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
        if (playFrom.isEmpty() || playUrl.isEmpty()) {
            Log.d(TAG, "播放源为空：playFrom=$playFrom, playUrl=$playUrl")
            return sources
        }
        
        Log.d(TAG, "[parsePlaySources] playFrom: $playFrom")
        Log.d(TAG, "[parsePlaySources] playUrl length: ${playUrl.length}")
        
        val sourceNames = playFrom.split("$$$").map { it.trim() }
        val sourceUrls = playUrl.split("$$$").map { it.trim() }
        
        Log.d(TAG, "[parsePlaySources] 解析出 ${sourceNames.size} 个播放源名称")
        
        val count = minOf(sourceNames.size, sourceUrls.size)
        for (i in 0 until count) {
            val name = sourceNames[i]
            val urlStr = sourceUrls[i]
            
            if (name.isEmpty()) continue
            
            if (urlStr.isEmpty()) continue
            
            val episodeParts = urlStr.split("#").filter { it.isNotEmpty() }
            
            // 同步 Web 端逻辑：检查播放源是否为 M3U8 格式，只保留 M3U8
            var isM3U8Source = false
            if (episodeParts.isNotEmpty()) {
                val firstPart = episodeParts.first().trim()
                if (firstPart.contains("$")) {
                    val firstEpisodeData = firstPart.split("$")
                    if (firstEpisodeData.size >= 2) {
                        val firstUrl = firstEpisodeData[1].trim().lowercase()
                        isM3U8Source = firstUrl.contains(".m3u8") || firstUrl.contains("m3u8")
                        Log.d(TAG, "[parsePlaySources] 播放源 '$name' 第一集 URL: ${firstUrl.take(100)}, M3U8: $isM3U8Source")
                    }
                }
            }
            
            // 过滤掉非 M3U8 的播放源
            if (!isM3U8Source) {
                Log.d(TAG, "[parsePlaySources] 过滤非 M3U8 播放源：$name")
                continue
            }
            
            val episodes = mutableListOf<Episode>()
            for (part in episodeParts) {
                val episodeData = part.split("$")
                if (episodeData.size >= 2) {
                    episodes.add(Episode(episodeData[0].trim(), episodeData[1].trim()))
                }
            }
            
            if (episodes.isNotEmpty()) {
                sources.add(PlaySource(name, episodes, 0))
                Log.d(TAG, "[parsePlaySources] 添加 M3U8 播放源：$name, 剧集数：${episodes.size}")
            }
        }
        
        Log.d(TAG, "[parsePlaySources] 最终返回 ${sources.size} 个 M3U8 播放源")
        return sources
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
    
    companion object {
        const val EXTRA_VIDEO_ID = "extra_video_id"
        const val EXTRA_VIDEO_TITLE = "extra_video_title"
        const val EXTRA_VIDEO_DATA = "extra_video_data"
        const val REQUEST_CODE_PLAY = 1001
    }
}
