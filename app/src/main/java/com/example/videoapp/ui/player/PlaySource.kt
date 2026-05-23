package com.example.videoapp.ui.player

data class PlaySource(
    val name: String,
    val episodes: List<Episode>,
    var status: Int = 0  // 0=待检测，1=成功，2=失败
)

data class Episode(
    val name: String,
    val url: String
)
