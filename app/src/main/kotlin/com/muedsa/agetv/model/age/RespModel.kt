package com.muedsa.agetv.model.age

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RespModel<E>(
    @SerialName("code")
    val code: Int,
    @SerialName("message")
    val message: String?,
    @SerialName("data")
    val data: E?
)