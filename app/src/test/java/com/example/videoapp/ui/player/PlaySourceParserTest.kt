package com.example.videoapp.ui.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaySourceParserTest {

    @Test
    fun `empty input returns empty list`() {
        assertTrue(PlaySourceParser.parse("", "").isEmpty())
        assertTrue(PlaySourceParser.parse("   ", "   ").isEmpty())
        assertTrue(PlaySourceParser.parse("线路一", "").isEmpty())
        assertTrue(PlaySourceParser.parse("", "第1集\$http://a.m3u8").isEmpty())
    }

    @Test
    fun `parses single channel with multiple episodes`() {
        val sources = PlaySourceParser.parse(
            "线路一",
            "第1集\$http://a.m3u8#第2集\$http://b.m3u8"
        )

        assertEquals(1, sources.size)
        assertEquals("线路一", sources[0].name)
        assertEquals(2, sources[0].episodes.size)
        assertEquals(Episode("第1集", "http://a.m3u8"), sources[0].episodes[0])
        assertEquals(Episode("第2集", "http://b.m3u8"), sources[0].episodes[1])
    }

    @Test
    fun `parses multiple channels separated by three dollars`() {
        val sources = PlaySourceParser.parse(
            "线路一\$\$\$线路二",
            "第1集\$http://a.m3u8\$\$\$第1集\$http://c.m3u8"
        )

        assertEquals(2, sources.size)
        assertEquals("线路一", sources[0].name)
        assertEquals("线路二", sources[1].name)
        assertEquals("http://c.m3u8", sources[1].episodes[0].url)
    }

    @Test
    fun `skips empty channel names and urls`() {
        val sources = PlaySourceParser.parse(
            "\$\$\$线路一\$\$\$",
            "\$\$\$第1集\$http://a.m3u8\$\$\$"
        )

        assertEquals(1, sources.size)
        assertEquals("线路一", sources[0].name)
    }

    @Test
    fun `filters out non m3u8 sources`() {
        // 首个剧集不是 m3u8：整个播放源被过滤（与 web 端 parsePlaySources 一致）
        val sources = PlaySourceParser.parse(
            "线路一",
            "第1集\$http://a.mp4#第2集\$http://b.m3u8"
        )

        assertTrue(sources.isEmpty())
    }

    @Test
    fun `filters source whose first episode has no url even if later is m3u8`() {
        // 首个剧集段无法解析出 URL：整个播放源被过滤
        val sources = PlaySourceParser.parse(
            "线路一",
            "坏数据#第1集\$http://a.m3u8"
        )

        assertTrue(sources.isEmpty())
    }

    @Test
    fun `keeps only m3u8 source among mixed sources`() {
        val sources = PlaySourceParser.parse(
            "线路一\$\$\$线路二",
            "第1集\$http://a.mp4\$\$\$第1集\$http://c.m3u8"
        )

        assertEquals(1, sources.size)
        assertEquals("线路二", sources[0].name)
        assertEquals("http://c.m3u8", sources[0].episodes[0].url)
    }

    @Test
    fun `trims whitespace in names and urls`() {
        val sources = PlaySourceParser.parse(
            " 线路一 ",
            " 第1集 \$ http://a.m3u8 "
        )

        assertEquals(1, sources.size)
        assertEquals("线路一", sources[0].name)
        assertEquals("第1集", sources[0].episodes[0].name)
        assertEquals("http://a.m3u8", sources[0].episodes[0].url)
    }

    @Test
    fun `preserves dollar sign inside video url`() {
        // 与 web 端 explode('$', part, 2) 一致：只按第一个 $ 分割，URL 内的 $ 保留
        val sources = PlaySourceParser.parse(
            "线路一",
            "第1集\$http://host/path\$token.m3u8"
        )

        assertEquals(1, sources.size)
        assertEquals("第1集", sources[0].episodes[0].name)
        assertEquals("http://host/path\$token.m3u8", sources[0].episodes[0].url)
    }

    @Test
    fun `returns empty when only malformed data present`() {
        val sources = PlaySourceParser.parse("线路一", "坏数据#也是坏数据")
        assertTrue(sources.isEmpty())
    }

    @Test
    fun `filterM3U8 keeps only m3u8 structured sources`() {
        val mp4Source = PlaySource("线路一", listOf(Episode("第1集", "http://a.mp4")))
        val m3u8Source = PlaySource("线路二", listOf(Episode("第1集", "http://b.m3u8")))
        val emptySource = PlaySource("线路三", emptyList())

        val filtered = PlaySourceParser.filterM3U8(listOf(mp4Source, m3u8Source, emptySource))

        assertEquals(1, filtered.size)
        assertEquals("线路二", filtered[0].name)
    }
}
