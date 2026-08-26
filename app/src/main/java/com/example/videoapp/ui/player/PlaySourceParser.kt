package com.example.videoapp.ui.player

/**
 * 播放源解析工具（纯函数，无 Android 依赖，便于单元测试）。
 *
 * 对应 MacCMS 采集格式：
 * - [playFrom]：渠道名之间用 `$$$` 分隔
 * - [playUrl]：渠道之间用 `$$$` 分隔；剧集之间用 `#` 分隔；剧集名与 URL 用 `$` 分隔
 *
 * 示例：
 * ```
 * playFrom = "线路一$$$线路二"
 * playUrl  = "第1集$http://a.m3u8#第2集$http://b.m3u8$$$第1集$http://c.m3u8"
 * ```
 */
object PlaySourceParser {

    /**
     * 解析出全部播放源；空渠道、空剧集会被跳过。
     */
    fun parse(playFrom: String, playUrl: String): List<PlaySource> {
        if (playFrom.isBlank() || playUrl.isBlank()) return emptyList()

        val sourceNames = playFrom.split("$$$").map { it.trim() }.filter { it.isNotEmpty() }
        val sourceUrls = playUrl.split("$$$").map { it.trim() }.filter { it.isNotEmpty() }

        val count = minOf(sourceNames.size, sourceUrls.size)
        val sources = mutableListOf<PlaySource>()
        for (i in 0 until count) {
            val episodes = parseEpisodes(sourceUrls[i])
            if (episodes.isNotEmpty()) {
                sources.add(PlaySource(name = sourceNames[i], episodes = episodes, status = 0))
            }
        }
        return sources
    }

    private fun parseEpisodes(urlStr: String): List<Episode> {
        return urlStr.split("#")
            .filter { it.isNotEmpty() }
            .mapNotNull { part ->
                val pieces = part.split("$").map { it.trim() }.filter { it.isNotEmpty() }
                if (pieces.size >= 2) Episode(name = pieces[0], url = pieces[1]) else null
            }
    }
}
