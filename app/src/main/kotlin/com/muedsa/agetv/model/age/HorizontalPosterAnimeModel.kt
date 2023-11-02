package com.muedsa.agetv.model.age

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HorizontalPosterAnimeModel(
    @SerialName("AID")
    val aid: Int,
    @SerialName("Title")
    val title: String,
    @SerialName("PicUrl")
    val picUrl: String
)