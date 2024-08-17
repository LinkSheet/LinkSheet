package fe.linksheet

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.PatternMatcher
import fe.linksheet.module.resolver.PackageHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertFalse
import kotlin.test.assertTrue


@RunWith(RobolectricTestRunner::class)
class PackageHandlerTest {
    private lateinit var packageHandler: PackageHandler

    @Before
    fun setup() {
        packageHandler = PackageHandler(
            queryIntentActivities = { intent, flags ->
                listOf()
            },
            isLinkSheetCompat = { false }
        )
    }

    @Test
    fun `url matches path`() {
        IntentFilter().apply {
            addAction(Intent.ACTION_VIEW)
            addCategory(Intent.CATEGORY_DEFAULT)
            addCategory(Intent.CATEGORY_BROWSABLE)
            addDataAuthority("github.com", null)
            addDataAuthority("*", null)
            addDataAuthority("*", null)
        }
    }

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
        return packageHandler.isLinkHandler(IntentFilter().apply(filter), Uri.parse(uri))
    }
}
