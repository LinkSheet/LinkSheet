package fe.linksheet.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.linksheet.UnitTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class HttpProtocolUtilTest : UnitTest{
    @Test
    fun `test maybePrependProtocol`() {
        assertThat("linksheet.app".maybePrependProtocol("https://")).isEqualTo("https://linksheet.app")
        assertThat("linksheet.app".maybePrependProtocol("https")).isEqualTo("https://linksheet.app")
        assertThat("https://linksheet.app".maybePrependProtocol("https://")).isEqualTo("https://linksheet.app")
        assertThat("https://linksheet.app".maybePrependProtocol("https")).isEqualTo("https://linksheet.app")
    }
}
