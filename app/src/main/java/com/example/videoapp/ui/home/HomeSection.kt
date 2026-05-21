package com.example.videoapp.ui.home

data class HomeSection(
    val title: String,
    val unifiedTypeId: Int,
    val videos: List<com.example.videoapp.data.model.Video>
)
