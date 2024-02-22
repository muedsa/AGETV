package com.muedsa.agetv.service

import com.muedsa.agetv.model.dandanplay.DanAnimesResp
import com.muedsa.agetv.model.dandanplay.DanBangumiResp
import com.muedsa.agetv.model.dandanplay.DanComments
import com.muedsa.agetv.model.dandanplay.DanSearchAnime
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DanDanPlayApiService {

    @GET("v2/search/anime")
    suspend fun searchAnime(
        @Query("keyword") keyword: String,
        @Query("type") type: String = ""
    ): DanAnimesResp<DanSearchAnime>

    @GET("v2/bangumi/{animeId}")
    suspend fun getAnime(
        @Path("animeId") animeId: Int
    ): DanBangumiResp

    @GET("v2/comment/{episodeId}")
    suspend fun getComment(
        @Path("episodeId") episodeId: Long,
        @Query("from") from: Int = 0,
        @Query("withRelated") withRelated: Boolean = false,
        @Query("chConvert") chConvert: Int = 0
    ): DanComments
}