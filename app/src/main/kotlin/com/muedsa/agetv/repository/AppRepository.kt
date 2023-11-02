package com.muedsa.agetv.repository


import androidx.core.net.toUri
import com.muedsa.agetv.exception.AgeParseException
import com.muedsa.agetv.model.AgePlayInfoModel
import com.muedsa.agetv.service.AgeApiService
import com.muedsa.agetv.service.AgePlayerService
import com.muedsa.agetv.service.DanDanPlayApiService
import com.muedsa.uitl.LogUtil
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val apiService: AgeApiService,
    private val playerService: AgePlayerService,
    private val danDanPlayApiService: DanDanPlayApiService,
) {

    suspend fun home() = apiService.homeList()

    suspend fun slipic() = apiService.slipic()

    suspend fun recommend() = apiService.recommend()

    suspend fun catalog(
        genre: String = "all",
        label: String = "all",
        letter: String = "all",
        order: String = "time",
        region: String = "all",
        resource: String = "all",
        season: String = "all",
        status: String = "all",
        year: String = "all",
        page: Int = 1,
        size: Int = 10
    ) = apiService.catalog(
        genre = genre,
        label = label,
        letter = letter,
        order = order,
        region = region,
        resource = resource,
        season = season,
        status = status,
        year = year,
        page = page,
        size = size
    )

    suspend fun search(query: String, page: Int = 1) = apiService.search(query = query, page = page)

    suspend fun update(page: Int = 1, size: Int = 30) = apiService.update(page = page, size = size)

    suspend fun rank(year: String = "") = apiService.rank(year = year)

    suspend fun detail(aid: Int) = apiService.detail(aid = aid)

    suspend fun comment(aid: Int, page: Int) = apiService.comment(aid, page)

    suspend fun playInfo(url: String): AgePlayInfoModel {
        val uri = url.toUri()
        val playInfo = playerService.getPlayInfo(uri.toString())
        LogUtil.fb("playInfo: $playInfo")
        if (playInfo.vUrl.isEmpty()) {
            throw AgeParseException("get vUrl is empty, from:$url")
        }
        val portStr = if ((uri.scheme === "http" && uri.port == 80)
            || (uri.scheme === "https" && uri.port == 80)
        ) {
            ""
        } else {
            ":${uri.port}"
        }
        playerService.decryptPlayUrl(playInfo, "${uri.scheme}://${uri.host}${portStr}${uri.path}")
        LogUtil.fb("decryptPlayUrl: $playInfo")
        return playInfo
    }

    suspend fun danDanPlaySearchAnime(query: String) = danDanPlayApiService.searchAnime(query)

    suspend fun danDanPlayGetAnime(animeId: Int) = danDanPlayApiService.getAnime(animeId)
}