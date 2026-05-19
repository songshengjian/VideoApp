package com.example.videoapp.data.model

import com.google.gson.annotations.SerializedName

data class Video(
    val vod_id: Int,
    val type_id: Int,
    val type_id_1: Int = 0,
    val group_id: Int = 0,
    val vod_name: String,
    val type_name: String = "",
    val vod_en: String = "",
    val vod_status: Int = 0,
    val vod_letter: String = "",
    val vod_color: String = "",
    val vod_tag: String = "",
    val vod_class: String = "",
    val vod_pic: String,
    val vod_pic_thumb: String? = null,
    val vod_pic_slide: String? = null,
    val vod_pic_screenshot: String? = null,
    val vod_actor: String = "",
    val vod_director: String = "",
    val vod_writer: String = "",
    val vod_behind: String = "",
    val vod_remarks: String = "",
    val vod_pubdate: String = "",
    val vod_total: Int = 0,
    val vod_serial: String = "",
    val vod_tv: String = "",
    val vod_weekday: String = "",
    val vod_is_vip: Int = 0,
    val vod_play_from: String = "",
    val vod_play_url: String = "",
    val vod_down_from: String = "",
    val vod_down_url: String = "",
    val vod_plot: String = "",
    val vod_plot_detail: String = "",
    val vod_time: String = "",
    val vod_hits: Int = 0,
    val vod_hits_day: Int = 0,
    val vod_hits_week: Int = 0,
    val vod_hits_month: Int = 0,
    val vod_score: Float = 0f,
    val vod_up: Int = 0,
    val vod_down: Int = 0,
    val vod_cash: Int = 0,
    val vod_is: Int = 0,
    val vod_lock: Int = 0,
    val vod_level: Int = 0,
    val vod_copyright: Int = 0,
    val vod_points: Int = 0,
    val vod_points_down: Int = 0,
    val vod_points_play: Int = 0,
    val vod_content: String = "",
    val vod_blurb: String = "",
    val vod_area: String = "",
    val vod_lang: String = "",
    val vod_year: String = "",
    val vod_version: String = "",
    val vod_state: String = "",
    val vod_author: String = "",
    val vod_jumpurl: String = "",
    val vod_addtime: String = "",
    val vod_time_add: Long = 0L,
    val vod_time_hits: Long = 0L,
    val vod_time_make: Long = 0L,
    val vod_trysee: Int = 0,
    val vod_relevel: Int = 0,
    val vod_remarks_relate: String = "",
    val vod_play_url_rel: String = "",
    val vod_down_url_rel: String = "",
    val vod_rel_vod: String = "",
    val vod_rel_art: String = "",
    val vod_content_status: String = ""
)

data class Category(
    val type_id: Int,
    val type_id_1: Int = 0,
    val type_name: String,
    val type_en: String = "",
    val type_sort: Int = 0,
    val type_mid: Int = 1,
    val type_pid: Int = 0,
    val type_status: Int = 0,
    val type_is: Int = 0,
    val type_lock: Int = 0,
    val type_level: Int = 0,
    val type_tpl: String = "",
    val type_tpl_list: String = "",
    val type_tpl_detail: String = "",
    val type_tpl_play: String = "",
    val type_tpl_down: String = "",
    val type_key: String = "",
    val type_des: String = "",
    val type_title: String = "",
    val type_pic: String = "",
    val type_remarks: String = "",
    val type_rel: String = "",
    val type_jumpurl: String = "",
    val type_addtime: String = "",
    val type_time: Long = 0L,
    val type_mid_1: Int = 0
)

data class VideoResponse(
    val code: Int,
    val msg: String,
    val list: List<Video> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val pagecount: Int = 1,
    val limit: String = ""
)

data class CategoryResponse(
    val code: Int,
    val msg: String,
    @SerializedName("class") val class_: List<Category> = emptyList()
)

data class User(
    val user_id: Int,
    val user_name: String,
    val user_email: String = "",
    val user_vip: Int = 0,
    val user_points: Int = 0,
    val user_login_time: Long = 0L,
    val user_login_ip: String = "",
    val user_end_time: Long = 0L,
    val user_instant: Int = 0
)

data class LoginRequest(
    val user_name: String,
    val user_pwd: String
)

data class RegisterRequest(
    val user_name: String,
    val user_pwd: String,
    val user_email: String = "",
    val user_phone: String = ""
)
