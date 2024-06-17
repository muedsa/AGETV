package com.muedsa.agetv.model.age

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class CatalogAnimeModel(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("uptodate")
    val upToDate: String,
    @SerialName("time")
    val time: Int,
    @SerialName("status")
    val status: String,
    @SerialName("play_time")
    val playTime: String? = "",
    @SerialName("type")
    val type: String,
    @SerialName("name_original")
    val nameOriginal: String,
    @SerialName("name_other")
    val nameOther: String,
    @SerialName("premiere")
    val premiere: String,
    @SerialName("writer")
    val writer: String,
    @SerialName("tags")
    val tags: String,
    @SerialName("company")
    val company: String,
    @SerialName("intro")
    val intro: String,
    @SerialName("tags_arr")
    @JsonNames("tagsArr")
    val tagsArr: List<String>,
    @SerialName("cover")
    val cover: String, // ratio = 256 X 365
)