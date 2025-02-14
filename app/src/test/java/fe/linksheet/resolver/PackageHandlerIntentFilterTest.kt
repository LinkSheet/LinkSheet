package fe.linksheet.resolver

import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.PatternMatcher
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.util.addDataPaths
import app.linksheet.testing.util.buildIntentFilter
import fe.linksheet.LinkSheetTest
import fe.linksheet.module.app.`package`.DefaultPackageIntentHandler
import fe.linksheet.module.app.`package`.PackageIntentHandler
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class PackageHandlerIntentFilterTest : LinkSheetTest {
    companion object {
        private val handler: PackageIntentHandler = DefaultPackageIntentHandler(
            queryIntentActivities = { _, _ -> listOf() },
            isLinkSheetCompat = { false },
            checkReferrerExperiment = { true }
        )

        private val githubIntentFilter = buildIntentFilter {
            addDataAuthority("github.com", null)
            addDataAuthority(".ghe.com", null)
            addDataScheme("https")

            addDataPaths(
                PatternMatcher.PATTERN_ADVANCED_GLOB,
                "/[^l].*", "/l[^o].*", "/lo[^g].*", "/log[^i].*", "/logi[^i].*",
                "/login[a-zA-Z0-9\\-\\_].*",
                "/l", "/lo", "/log", "/logi"
            )
        }

        private fun isLinkHandler(uri: String, filter: IntentFilter): Boolean {
            return handler.isLinkHandler(filter, Uri.parse(uri))
        }
    }

    @Test
    fun test() {
        assertFalse(isLinkHandler("https://google.com", IntentFilter()))

        assertFalse(isLinkHandler("https://google.com", buildIntentFilter {
            addDataAuthority("*", null)
        }))

        assertFalse(isLinkHandler("https://test.com/test.zip", buildIntentFilter {
            addDataAuthority("*", null)
            addDataPath(".*\\.zip", PatternMatcher.PATTERN_ADVANCED_GLOB)
        }))

        assertTrue(isLinkHandler("https://github.com", githubIntentFilter))
        assertTrue(isLinkHandler("https://github.com/LinkSheet/LinkSheet", githubIntentFilter))
        assertTrue(isLinkHandler("https://github.com/KieronQuinn/DarQ/releases/latest", githubIntentFilter))

        assertFalse(isLinkHandler("https://google.com", githubIntentFilter))
    }
}
