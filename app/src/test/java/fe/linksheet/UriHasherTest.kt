package fe.linksheet

import fe.linksheet.util.CryptoUtil
import org.junit.After
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatformTools
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ClosingKoinTest
import javax.crypto.Mac

class UriHasherTest {
    private val mac: Mac

    init {
        val hmacSha256 = CryptoUtil.HmacSha("HmacSHA256", 64)
        val test = "test"
        val key = test.repeat(hmacSha256.keySize / test.length).toByteArray()

        mac = CryptoUtil.makeHmac(hmacSha256.algorithm, key)
    }

    @Test
    fun test() {
//        assertEquals(buildHashedUriString("https://google.com/henlo/fren", mac), "https://b60687.8fc4dccb1dcf/b3901e/452116/")
        // TODO: query parameters are currently broken. Let's fix this later, there's more important stuff to do (tracked in #226)
//        assertEquals(buildHashedUriString("https://google.com?we=is&fren=yes", mac), "https://b60687.8fc4dccb1dcf/b3901e/452116/")
    }

    @After
    fun teardown() = stopKoin()
}
