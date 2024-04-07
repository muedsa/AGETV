package com.muedsa.util

import com.muedsa.uitl.encryptAES128CBCPKCS7
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test
import java.security.Security

class StrCodecKtTest {

    init {
        Security.removeProvider("BC")
        // Confirm that positioning this provider at the end works for your needs!
        Security.addProvider(BouncyCastleProvider())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun decryptPlayUrlTest() {
        val key = "ni po jie ni ** "
        println("muedsa".encryptAES128CBCPKCS7(key, key).toHexString())
    }
}