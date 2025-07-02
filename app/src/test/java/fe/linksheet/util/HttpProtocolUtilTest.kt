package fe.linksheet.util

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class HttpProtocolUtilTest : BaseUnitTest {
    @org.junit.Test
    fun `test maybePrependProtocol`() {
        assertThat("linksheet.app".maybePrependProtocol("https://")).isEqualTo("https://linksheet.app")
        assertThat("linksheet.app".maybePrependProtocol("https")).isEqualTo("https://linksheet.app")
        assertThat("https://linksheet.app".maybePrependProtocol("https://")).isEqualTo("https://linksheet.app")
        assertThat("https://linksheet.app".maybePrependProtocol("https")).isEqualTo("https://linksheet.app")
    }
}
