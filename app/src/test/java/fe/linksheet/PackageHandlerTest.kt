package fe.linksheet

import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.PatternMatcher
import fe.linksheet.module.resolver.PackageHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
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

    @Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
    @Test
    fun `url matches path`() {
        val github: IntentFilter.() -> Unit = {
            addAction(Intent.ACTION_VIEW)
            addCategory(Intent.CATEGORY_DEFAULT)
            addCategory(Intent.CATEGORY_BROWSABLE)
            addDataAuthority("github.com", null)
            addDataAuthority(".ghe.com", null)

            setOf(
                "/[^l].*",
                "/l[^o].*",
                "/lo[^g].*",
                "/log[^i].*",
                "/logi[^i].*",
                "/login[a-zA-Z0-9\\-\\_].*",
                "/l",
                "/lo",
                "/log",
                "/logi"
            ).forEach { addDataPath(it, PatternMatcher.PATTERN_ADVANCED_GLOB) }

            addDataScheme("https")
        }

        assertTrue(runTest("https://github.com", github))
        assertTrue(runTest("https://github.com/LinkSheet/LinkSheet", github))
        assertTrue(runTest("https://github.com/KieronQuinn/DarQ/releases/latest", github))

        assertFalse(runTest("https://google.com", github))
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
