package fe.linksheet.util.web

import android.net.Uri
import android.net.compatHost
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class HostPublicityTest : BaseUnitTest {
    companion object {
        private val data = mapOf(
            "localhost" to HostType.Local,
            "127.0.0.1" to HostType.Local,
            ".local" to null,
            "[::1]" to HostType.Local,
            "0.0.0.0" to HostType.Local,
            "linksheet.local" to HostType.Mdns,
            "linksheet.test" to HostType.Mdns,
            "linksheet.example" to HostType.Mdns,
            "linksheet.invalid" to HostType.Mdns,
            "linksheet.localhost" to HostType.Mdns,
            // Looks like an IP, but is invalid -> determined to be a host name
            "300.1.1.1" to HostType.Host,
            "1.2.3.4.5" to HostType.Host,
            "172.16.0.1" to HostType.Local,
            "10.4.21.1" to HostType.Local,
            "192.168.1.1" to HostType.Local,
            "172.1.1.1" to HostType.Host,
            "1.2.3.4" to HostType.Host,
            "100.212.103.1" to HostType.Host,
        )
    }

    @org.junit.Test
    fun test() {
        for ((hostname, expected) in data) {
            val actual = HostUtil.getHostType(Uri.parse("http://$hostname/dummy"))
            assertEquals(expected, actual, message = hostname)
        }
    }

    @Config(sdk = [Build.VERSION_CODES.P])
    @org.junit.Test
    fun testIpv6PreApi29Q() {
        // Uri#host does not properly parse IPv6 hosts on < 29 / Q (https://issuetracker.google.com/issues/37069493)
        val uri = Uri.parse("http://[::1]")
        assertEquals("[", uri.host)
        assertEquals("[::1]", uri.compatHost?.value)
    }
}
