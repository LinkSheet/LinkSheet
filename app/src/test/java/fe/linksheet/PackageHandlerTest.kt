package fe.linksheet

import android.content.IntentFilter
import android.net.Uri
import android.os.PatternMatcher
import fe.linksheet.module.resolver.PackageHandler
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertFalse
import kotlin.test.assertTrue


@RunWith(RobolectricTestRunner::class)
class PackageHandlerTest {

    @Test
    fun testIntentFilter() {
        assertFalse(runTest("https://google.com") {})

        assertFalse(runTest("https://google.com") {
            addDataAuthority("*", null)
        })

        assertTrue(runTest("https://test.com/test.zip") {
            addDataAuthority("*", null)
            addDataPath(".*\\.zip", PatternMatcher.PATTERN_ADVANCED_GLOB)
        })
    }

    private fun runTest(uri: String, filter: IntentFilter.() -> Unit): Boolean {
        return PackageHandler.isLinkHandler(IntentFilter().apply(filter), Uri.parse(uri))
    }
}
