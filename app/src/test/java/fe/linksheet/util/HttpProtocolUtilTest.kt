package fe.linksheet.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.linksheet.testlib.core.RobolectricTest
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class HttpProtocolUtilTest : RobolectricTest {
    @JunitTest
    fun `test maybePrependProtocol`() {
        assertThat("linksheet.app".maybePrependProtocol("https://")).isEqualTo("https://linksheet.app")
        assertThat("linksheet.app".maybePrependProtocol("https")).isEqualTo("https://linksheet.app")
        assertThat("https://linksheet.app".maybePrependProtocol("https://")).isEqualTo("https://linksheet.app")
        assertThat("https://linksheet.app".maybePrependProtocol("https")).isEqualTo("https://linksheet.app")
    }
}
