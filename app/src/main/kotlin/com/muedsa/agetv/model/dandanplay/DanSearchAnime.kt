package com.muedsa.agetv.model.dandanplay

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DanSearchAnime(
    @SerialName("animeId") val animeId: Int,
    @SerialName("animeTitle") val animeTitle: String,
    @SerialName("type") val type: String,
    @SerialName("typeDescription") val typeDescription: String,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("startDate") val startDate: String,
    @SerialName("episodeCount") val episodeCount: Int,
    @SerialName("rating") val rating: Float,
    @SerialName("isFavorited") val isFavorited: Boolean
)
