package com.muedsa.agetv.model.age

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentModel(
    @SerialName("sid")
    val sid: Int,
    @SerialName("content")
    val content: Int,
    @SerialName("time")
    val time: String,
    @SerialName("uid")
    val uid: Int,
    @SerialName("username")
    val username: Int,
    @SerialName("cid")
    val cid: Int,
    @SerialName("ip")
    val ip: String,
    @SerialName("status")
    val status: Int,
    @SerialName("floor")
    val floor: Int
)
