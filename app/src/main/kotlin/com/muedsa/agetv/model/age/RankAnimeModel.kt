package com.muedsa.agetv.model.age

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankAnimeModel(
    @SerialName("NO")
    val no: Int,
    @SerialName("AID")
    val aid: Int,
    @SerialName("Title")
    val title: String,
    @SerialName("CCnt")
    val ccNt: String,
)
