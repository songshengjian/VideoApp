package com.example.videoapp.ui.player

/**
 * 播放源解析工具（纯函数，无 Android 依赖，便于单元测试）。
 *
 * 逻辑与 Web 端一致：
 * - [playFrom]：渠道名之间用 `$$$` 分隔
 * - [playUrl]：渠道之间用 `$$$` 分隔；剧集之间用 `#` 分隔；剧集名与 URL 用第一个 `$` 分割（URL 本身可能含 `$`）
 * - 只保留首个剧集 URL 为 M3U8 的播放源，非 M3U8 源直接过滤（与 web 端 parsePlaySources 一致）
 *
 * 示例：
 * ```
 * playFrom = "线路一$$$线路二"
 * playUrl  = "第1集$http://a.m3u8#第2集$http://b.m3u8$$$第1集$http://c.m3u8"
 * ```
 */
object PlaySourceParser {

    /**
     * 解析出全部播放源；空渠道、空剧集、非 M3U8 源会被跳过。
     */
    fun parse(playFrom: String, playUrl: String): List<PlaySource> {
        if (playFrom.isBlank() || playUrl.isBlank()) return emptyList()

        val sourceNames = playFrom.split("$$$").map { it.trim() }.filter { it.isNotEmpty() }
        val sourceUrls = playUrl.split("$$$").map { it.trim() }.filter { it.isNotEmpty() }

        val count = minOf(sourceNames.size, sourceUrls.size)
        val sources = mutableListOf<PlaySource>()
        for (i in 0 until count) {
            // 与 web 端一致：只保留 M3U8 播放源，非 M3U8 源过滤掉
            if (!isM3U8SourceUrl(sourceUrls[i])) continue

            val episodes = parseEpisodes(sourceUrls[i])
            if (episodes.isNotEmpty()) {
                sources.add(PlaySource(name = sourceNames[i], episodes = episodes, status = 0))
            }
        }
        return sources
    }

    /**
     * 对已结构化的播放源列表做 M3U8 过滤（用于 detail-all 返回的 play_sources）
     */
    fun filterM3U8(sources: List<PlaySource>): List<PlaySource> {
        return sources.filter { isM3U8Url(it.episodes.firstOrNull()?.url.orEmpty()) }
    }

    /**
     * 与 web 端一致：首个剧集 URL 包含 .m3u8 或 m3u8 才视为有效 M3U8 源。
     */
    private fun isM3U8Url(url: String): Boolean {
        val lower = url.trim().lowercase()
        return lower.contains(".m3u8") || lower.contains("m3u8")
    }

    /**
     * 检查原始剧集串（`剧集名$URL#剧集名$URL`）的首个 URL 是否为 M3U8
     */
    private fun isM3U8SourceUrl(urlStr: String): Boolean {
        val firstPart = urlStr.split("#").firstOrNull { it.isNotEmpty() } ?: return false
        val dollarIndex = firstPart.indexOf('$')
        if (dollarIndex < 0) return false

        return isM3U8Url(firstPart.substring(dollarIndex + 1))
    }

    /**
     * 剧集解析：与 web 端 explode('$', $part, 2) 一致，只按第一个 $ 分割，
     * 避免 URL 本身包含 $ 时被截断。
     */
    private fun parseEpisodes(urlStr: String): List<Episode> {
        return urlStr.split("#")
            .filter { it.isNotEmpty() }
            .mapNotNull { part ->
                val dollarIndex = part.indexOf('$')
                if (dollarIndex <= 0) return@mapNotNull null

                val name = part.substring(0, dollarIndex).trim()
                val url = part.substring(dollarIndex + 1).trim()
                if (name.isEmpty() || url.isEmpty()) null else Episode(name, url)
            }
    }
}
