package com.muedsa.agetv.model.age

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeDetailModel(
    @SerialName("id")
    val id: Int,

    @SerialName("name_other")
    val nameOther: String,

    @SerialName("company")
    val company: String,

    @SerialName("name")
    val name: String,

    @SerialName("type")
    val type: String,

    @SerialName("writer")
    val writer: String,

    @SerialName("name_original")
    val nameOriginal: String,

    @SerialName("plot")
    val plot: String,

    @SerialName("plot_arr")
    val plotArr: List<String>,

    @SerialName("playlists")
    val playLists: Map<String, List<List<String>>>,

    @SerialName("area")
    val area: String,

    @SerialName("letter")
    val letter: String,

    @SerialName("website")
    val website: String,

    @SerialName("star")
    val star: Int,

    @SerialName("status")
    val status: String,

    @SerialName("uptodate")
    val upToDate: String,

    @SerialName("time_format_1")
    val timeFormat1: String,

    @SerialName("time_format_2")
    val timeFormat2: String,

    @SerialName("time_format_3")
    val timeFormat3: String,

    @SerialName("time")
    val time: Long,

    @SerialName("tags")
    val tags: String,

    @SerialName("tags_arr")
    val tagsArr: List<String>,

    @SerialName("intro")
    val intro: String,

    @SerialName("intro_html")
    val introHtml: String,

    @SerialName("intro_clean")
    val introClean: String,

    @SerialName("series")
    val series: String,

//    @SerialName("net_disk")
//    val netDisk: String,

    @SerialName("resource")
    val resource: String,

    @SerialName("year")
    val year: Int,

    @SerialName("season")
    val season: Int,

    @SerialName("premiere")
    val premiere: String,

    @SerialName("rank_cnt")
    val rankCnt: String,

    @SerialName("cover")
    val cover: String, // ratio 256X356

    @SerialName("comment_cnt")
    val commentCnt: String,

    @SerialName("collect_cnt")
    val collectCnt: String
)
