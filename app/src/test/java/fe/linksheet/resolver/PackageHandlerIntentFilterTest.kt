package fe.linksheet.resolver

import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.PatternMatcher
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.module.resolver.PackageHandler
import fe.linksheet.resolver.util.addDataPaths
import fe.linksheet.resolver.util.buildIntentFilter
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config
import kotlin.test.assertFalse
import kotlin.test.assertTrue


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class PackageHandlerIntentFilterTest {
    companion object {
        private val handler: PackageHandler = PackageHandler(
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

    @After
    fun teardown() = stopKoin()
}
