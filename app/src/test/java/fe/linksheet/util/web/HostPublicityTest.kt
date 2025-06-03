package fe.linksheet.util.web

import android.net.Uri
import android.net.compatHost
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.testlib.core.RobolectricTest
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class HostPublicityTest : RobolectricTest {
    companion object {
        private val data = mapOf(
            "localhost" to false,
            "127.0.0.1" to false,
            ".local" to false,
            "::1" to false,
            "0.0.0.0" to false,
            "linksheet.local" to false,
            "linksheet.test" to false,
            "linksheet.example" to false,
            "linksheet.invalid" to false,
            "linksheet.localhost" to false,
            "300.1.1.1" to false,
            "1.2.3.4.5" to false,
            "172.16.0.1" to false,
            "10.4.21.1" to false,
            "192.168.1.1" to false,
            "172.1.1.1" to true,
            "1.2.3.4" to true,
            "100.212.103.1" to true,
        )
    }

    @JunitTest
    fun test() {
        data.forEach { (hostname, expected) ->
            val actual = HostUtil.isAccessiblePublicly(Uri.parse("http://$hostname"))
            assertEquals(expected, actual, message = hostname)
        }
    }

    @Config(sdk = [Build.VERSION_CODES.P])
    @JunitTest
    fun testIpv6PreApi28Q() {
        // Uri#host does not properly parse IPv6 hosts on < 28 / Q (https://issuetracker.google.com/issues/37069493)
        val uri = Uri.parse("http://[::1]")
        assertEquals("[", uri.host)
        assertEquals("[::1]", uri.compatHost?.value)
    }
}
