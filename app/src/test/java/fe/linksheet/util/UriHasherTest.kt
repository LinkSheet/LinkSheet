package fe.linksheet.util

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import javax.crypto.Mac
import kotlin.intArrayOf

//@RunWith(AndroidJUnit4::class)
//@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class UriHasherTest : BaseUnitTest {
    private val mac: Mac

    init {
        val hmacSha256 = CryptoUtil.HmacSha("HmacSHA256", 64)
        val test = "test"
        val key = test.repeat(hmacSha256.keySize / test.length).toByteArray()

        mac = CryptoUtil.makeHmac(hmacSha256.algorithm, key)
    }

//    @org.junit.Test
//    fun test() {
////        assertEquals(buildHashedUriString("https://google.com/henlo/fren", mac), "https://b60687.8fc4dccb1dcf/b3901e/452116/")
//        // TODO: query parameters are currently broken. Let's fix this later, there's more important stuff to do (tracked in #226)
////        assertEquals(buildHashedUriString("https://google.com?we=is&fren=yes", mac), "https://b60687.8fc4dccb1dcf/b3901e/452116/")
//    }
}
