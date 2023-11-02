package com.muedsa.util

import com.muedsa.uitl.encryptAES128CBCPKCS7
import org.junit.Test

class StrCodecKtTest {

    @Test
    fun decryptPlayUrlTest() {
        val key = "ni po jie ni nb "
        println("muedsa".encryptAES128CBCPKCS7(key, key))
    }
}