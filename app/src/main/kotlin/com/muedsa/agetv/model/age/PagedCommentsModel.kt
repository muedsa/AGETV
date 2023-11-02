package com.muedsa.agetv.model.age

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedCommentsModel(
    @SerialName("comments")
    val comments: List<CommentModel>,
    @SerialName("pagination")
    val pagination: PageInfoModel
)