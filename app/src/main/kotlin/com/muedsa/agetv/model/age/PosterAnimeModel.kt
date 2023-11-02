package com.muedsa.agetv.model.age

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosterAnimeModel(
    @SerialName("AID")
    val aid: Int,
    @SerialName("Href")
    val href: String,
    @SerialName("NewTitle")
    val newTitle: String,
    @SerialName("PicSmall")
    val picSmall: String, // ratio = 256 X 365
    @SerialName("Title")
    val title: String
)
