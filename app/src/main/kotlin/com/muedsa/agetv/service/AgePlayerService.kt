package com.muedsa.agetv.service

import com.google.common.net.HttpHeaders
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.muedsa.agetv.AgeMobileUrl
import com.muedsa.agetv.AgeMobileUrlBase64
import com.muedsa.agetv.BuildConfig
import com.muedsa.agetv.model.AgePlayInfoModel
import com.muedsa.uitl.ChromeUserAgent
import com.muedsa.uitl.LenientJson
import com.muedsa.uitl.LogUtil
import com.muedsa.uitl.decryptAES128CBCPKCS7
import com.muedsa.uitl.encryptAES128CBCPKCS7
import com.muedsa.uitl.md5
import kotlinx.serialization.encodeToString
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Retrofit
import java.net.URLDecoder
import java.util.UUID


class AgePlayerService {

    suspend fun getPlayInfo(url: String): AgePlayInfoModel {
        val doc = fetchGet(url)
        val head = doc.head()
        val body = doc.body()
        val key1 = head.selectFirst("meta[http-equiv=\"Content-Type\"]")
            ?.attr("id")
            ?: ""
        val key2 = head.selectFirst("meta[name=\"viewport\"]")
            ?.attr("id")
            ?.replace("viewport", "")
            ?: ""
        val script = body.selectFirst("script")?.html() ?: ""
        return AgePlayInfoModel(
            pageUrl = url,
            metaKey = "$key1$key2",
            host = url.toHttpUrl().host,
            vUrl = getJsStringVar("Vurl", script),
            version = getJsStringVar("Version", script),
            time = getJsStringVar("Time", script),
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun decryptPlayUrl(playInfo: AgePlayInfoModel, baseUrl: String) {
        if (playInfo.vUrl.startsWith("https://")
            || playInfo.vUrl.startsWith("http://")
        ) {
            playInfo.realUrl = playInfo.vUrl
            playInfo.realUrlType = if (playInfo.vUrl.contains(".m3u8")) "hls" else "mp4"
        } else {
            val uuid = UUID.randomUUID().toString().uppercase()
            val req = AgeVipPlayerApiService.ApiRequest(
                url = playInfo.vUrl,
                host = playInfo.host,
                referer = AgeMobileUrlBase64,
                time = playInfo.time
            )
            LogUtil.fb("decrypt api req: $req")
            val encryptReq = LenientJson.encodeToString(req)
                .encryptAES128CBCPKCS7(WASM_AES_KEY, WASM_AES_KEY)
                .toHexString(HexFormat.UpperCase)
            LogUtil.fb("api baseUrl: $baseUrl")
            val resp = getVipApiService(baseUrl).api(
                params = encryptReq,
                uuid = uuid,
                time = playInfo.time,
                version = playInfo.version,
                sign = "${playInfo.host} | $uuid | ${playInfo.time} | ${playInfo.version} | $encryptReq"
                    .encryptAES128CBCPKCS7(WASM_AES_KEY, WASM_AES_KEY)
                    .toHexString(HexFormat.UpperCase),
                referer = playInfo.pageUrl
            )
            LogUtil.fb("decrypt api resp: $resp")
            if (resp.status == 1) {
                val decryptData = if (resp.code == 10) {
                    val keyAndIv = "${resp.code}${playInfo.metaKey}${resp.appKey}${resp.version}"
                        .md5()
                        .toHexString()
                    resp.data.hexToByteArray(HexFormat.UpperCase).decryptAES128CBCPKCS7(
                        keyAndIv.substring(0, 16),
                        keyAndIv.substring(16, 32)
                    ).toString(Charsets.UTF_8)

                } else {
                    val keyAndIv = "${resp.code}${resp.appKey}${resp.version}"
                        .md5()
                        .toHexString()
                    resp.data.hexToByteArray(HexFormat.UpperCase).decryptAES128CBCPKCS7(
                        keyAndIv.substring(0, 16),
                        keyAndIv.substring(16, 32)
                    ).toString(Charsets.UTF_8)
                }
                LogUtil.fb("decryptData: $decryptData")
                val playUrlResp =
                    LenientJson.decodeFromString<AgeVipPlayerApiService.PlayUrlResp>(decryptData)
                if (playUrlResp.code == SUCCESS_CODE) {
                    playInfo.realUrl = playUrlResp.url.let {
                        val encodeUrl = if (resp.code == 10) {
                            it.hexToByteArray(HexFormat.UpperCase)
                                .decryptAES128CBCPKCS7(WASM_AES_KEY, WASM_AES_KEY)
                                .toString(Charsets.UTF_8)
                        } else it
                        URLDecoder.decode(encodeUrl, Charsets.UTF_8.name())
                    }
                    playInfo.realUrlType = playUrlResp.type
                }
            }
        }
    }

    private fun getJsStringVar(field: String, content: String): String {
        val pattern = "var\\s+$field\\s*=\\s*['\"](.*?)['\"]\\s*;*".toRegex()
        return pattern.find(content)?.groups.let {
            if (!it.isNullOrEmpty() && it.size > 1) {
                it[1]?.value ?: ""
            } else ""
        }
    }

    private fun fetchGet(url: String): Document {
        return Jsoup.connect(url)
            .header(
                HttpHeaders.USER_AGENT,
                ChromeUserAgent
            )
            .header(
                HttpHeaders.REFERER,
                AgeMobileUrl
            )
            .timeout(TIMEOUT_MS)
            .get()
    }

    private fun getVipApiService(baseUrl: String): AgeVipPlayerApiService {
        if (DEFAULT_RETROFIT == null) {
            createDefaultRetrofit(baseUrl)
        }
        return if (DEFAULT_RETROFIT!!.baseUrl().toString() == baseUrl) {
            DEFAULT_VIP_API_SERVICE!!
        } else {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(LenientJson.asConverterFactory("image/vnd.microsoft.icon".toMediaType()))
                .client(OkHttpClient.Builder().addInterceptor {
                    it.proceed(
                        it.request().newBuilder()
                            .header(HttpHeaders.USER_AGENT, ChromeUserAgent)
                            .header(HttpHeaders.REFERER, AgeMobileUrl).build()
                    )
                }.also {
                    if (BuildConfig.DEBUG) {
                        val loggingInterceptor = HttpLoggingInterceptor()
                        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                        it.addInterceptor(loggingInterceptor)
                    }
                }.build())
                .build()
            retrofit.create(AgeVipPlayerApiService::class.java)
        }
    }

    @Synchronized
    private fun createDefaultRetrofit(baseUrl: String) {
        if (DEFAULT_RETROFIT == null) {
            DEFAULT_RETROFIT = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(LenientJson.asConverterFactory("image/vnd.microsoft.icon".toMediaType()))
                .client(OkHttpClient.Builder().addInterceptor {
                    it.proceed(
                        it.request().newBuilder()
                            .header(HttpHeaders.USER_AGENT, ChromeUserAgent)
                            .header(HttpHeaders.REFERER, AgeMobileUrl).build()
                    )
                }.also {
                    if (BuildConfig.DEBUG) {
                        val loggingInterceptor = HttpLoggingInterceptor()
                        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                        it.addInterceptor(loggingInterceptor)
                    }
                }.build())
                .build()
            DEFAULT_VIP_API_SERVICE = DEFAULT_RETROFIT!!.create(AgeVipPlayerApiService::class.java)
        }
    }

    companion object {
        private var DEFAULT_RETROFIT: Retrofit? = null
        private var DEFAULT_VIP_API_SERVICE: AgeVipPlayerApiService? = null
        private const val WASM_AES_KEY = "ni po jie ni nb "
        const val TIMEOUT_MS = 10 * 1000

        const val SUCCESS_CODE = 200
    }
}