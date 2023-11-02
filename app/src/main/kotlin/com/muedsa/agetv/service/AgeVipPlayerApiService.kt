package com.muedsa.agetv.service

import com.google.common.net.HttpHeaders
import com.muedsa.uitl.ChromeUserAgent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AgeVipPlayerApiService {

    @POST("vip/Api.php")
    @FormUrlEncoded
    @Headers(
        "${HttpHeaders.X_REQUESTED_WITH}: XMLHttpRequest",
        "${HttpHeaders.USER_AGENT}: $ChromeUserAgent"
    )
    suspend fun api(
        @Field("Params") params: String,
        @Header("Video-Parse-Uuid") uuid: String,
        @Header("Video-Parse-Time") time: String,
        @Header("Video-Parse-Version") version: String,
        @Header("Video-Parse-Sign") sign: String,
        @Header(HttpHeaders.REFERER) referer: String
    ): ApiResponse


    @Serializable
    data class ApiRequest(
        @SerialName("url") val url: String,
        @SerialName("wap") val wap: String = "0",
        @SerialName("ios") val ios: String = "0",
        @SerialName("host") val host: String,
        @SerialName("referer") val referer: String,
        @SerialName("time") val time: String
    )

    @Serializable
    data class ApiResponse(
        @SerialName("Status") val status: Int,
        @SerialName("Appkey") val appKey: String,
        @SerialName("Version") val version: String,
        @SerialName("Code") val code: Int,
        @SerialName("Data") val data: String,
    )

    @Serializable
    data class PlayUrlResp(
        @SerialName("code") val code: Int = -200,
        @SerialName("msg") val msg: String = "",
        @SerialName("success") val success: Int = 0,
        @SerialName("player") val player: String = "",
        @SerialName("type") val type: String = "",
        @SerialName("url") val url: String = "",
        @SerialName("title") val title: String = ""
    )
}