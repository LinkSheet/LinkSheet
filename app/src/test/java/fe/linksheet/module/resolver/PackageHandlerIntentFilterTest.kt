package fe.linksheet.module.resolver

import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.PatternMatcher
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.util.addDataPaths
import app.linksheet.testing.util.buildIntentFilter
import app.linksheet.feature.app.pkg.DefaultPackageIntentHandler
import app.linksheet.feature.app.pkg.PackageIntentHandler
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class PackageHandlerIntentFilterTest : BaseUnitTest  {
    companion object {
        private val handler: PackageIntentHandler = DefaultPackageIntentHandler(
            queryIntentActivities = { _, _ -> listOf() },
            resolveActivity = { _, _ -> null },
            isLinkSheetCompat = { false },
            isSelf = { false },
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

    @org.junit.Test
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
