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
    fun `skips malformed episodes without url`() {
        val sources = PlaySourceParser.parse(
            "线路一",
            "坏数据#第1集\$http://a.m3u8"
        )

        assertEquals(1, sources.size)
        assertEquals(1, sources[0].episodes.size)
        assertEquals("第1集", sources[0].episodes[0].name)
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
    fun `returns empty when only malformed data present`() {
        val sources = PlaySourceParser.parse("线路一", "坏数据#也是坏数据")
        assertTrue(sources.isEmpty())
    }
}
