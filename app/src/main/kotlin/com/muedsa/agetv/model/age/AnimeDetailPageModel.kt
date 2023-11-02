package com.muedsa.agetv.model.age

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeDetailPageModel(
    @SerialName("video")
    val video: AnimeDetailModel,

    @SerialName("series")
    val series: List<PosterAnimeModel> = emptyList(),

    @SerialName("similar")
    val similar: List<PosterAnimeModel> = emptyList(),

    @SerialName("player_label_arr")
    val playerLabelArr: Map<String, String>,

    @SerialName("player_vip")
    val playerVip: String,

    @SerialName("player_jx")
    val playerJx: Map<String, String>
) {
    private val playerVipList get() = playerVip.split(",")

    fun isVip(player: String) = playerVipList.contains(player)
}
