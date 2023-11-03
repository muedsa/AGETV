package com.muedsa.agetv.service

import com.muedsa.agetv.exception.AgeParseException
import kotlinx.coroutines.runBlocking
import org.junit.Test


class AgePlayerServiceTest {

    private val service = AgePlayerService()

//    @Test
//    fun getPlayInfoTest() {
//        runBlocking {
//            val baseUrl = "https://43.240.74.134:8443/vip/?url="
//            val param = "age_f382WV3AMFb1D%2FX2k5HO6m%2FjJxWGHSNwtMnyxFFeulNHddlKfzLiFZRY9md2wwxuM8Z%2BfkAchU98L%2BXRCcNRCaFZG9eTMJZODOgSUH8"
//            val playInfo = service.getPlayInfo("$baseUrl$param")
//            println("$playInfo")
//        }
//    }

    @Test
    fun decryptPlayUrlTest() {
        runBlocking {
            val baseUrl = "https://43.240.74.134:8443/vip/?url="
            val param =
                "age_f382WV3AMFb1D%2FX2k5HO6m%2FjJxWGHSNwtMnyxFFeulNHddlKfzLiFZRY9md2wwxuM8Z%2BfkAchU98L%2BXRCcNRCaFZG9eTMJZODOgSUH8"
            val playInfo = service.getPlayInfo("$baseUrl$param")
            println("$playInfo")
            if (playInfo.vUrl.isEmpty()) {
                throw AgeParseException("vurl is empty")
            }
            val playUrl = service.decryptPlayUrl(playInfo, "https://43.240.74.134:8443/vip/")
            println(playUrl)
        }
    }
}