package com.muedsa.agetv.model

data class AgePlayInfoModel(
    val pageUrl: String,
    val metaKey: String,
    val host: String,
    val vUrl: String,
    val version: String,
    val time: String,
    var realUrl: String = "",
    var realUrlType: String = ""
)