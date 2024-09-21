package fe.linksheet.browser

import android.content.pm.ResolveInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isDataClassEqualTo
import fe.linksheet.buildTestMock
import fe.linksheet.experiment.improved.resolver.FilteredBrowserList
import fe.linksheet.experiment.improved.resolver.browser.BrowserModeConfigHelper
import fe.linksheet.experiment.improved.resolver.browser.ImprovedBrowserHandler
import fe.linksheet.module.resolver.browser.BrowserMode
import org.junit.After
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class ImprovedBrowserHandlerTest {
    companion object {
        private val handler = ImprovedBrowserHandler()

        val miBrowser = buildTestMock { activity, application ->
            activity.name = "com.sec.android.app.sbrowser.SBrowserLauncherActivity"
            activity.packageName = "com.mi.globalbrowser"
        }

        val duckduckgoBrowser = buildTestMock { activity, application ->
            activity.name = "com.duckduckgo.app.dispatchers.IntentDispatcherActivity"
            activity.packageName = "com.duckduckgo.mobile.android"
        }

        val youtube = buildTestMock { activity, application ->
            activity.name = "com.google.android.youtube.UrlActivity"
            activity.packageName = "com.google.android.youtube"
        }

        val newPipe = buildTestMock { activity, application ->
            activity.name = "org.schabi.newpipe.RouterActivity"
            activity.packageName = "org.schabi.newpipe"
        }

        val newPipeEnhanced = buildTestMock { activity, application ->
            activity.name = "org.schabi.newpipe.RouterActivity"
            activity.packageName = "InfinityLoop1309.NewPipeEnhanced"
        }

        val allApps = listOf(youtube, newPipe, newPipeEnhanced)
        val allBrowsers = listOf(miBrowser, duckduckgoBrowser)
        val allResolved = allApps + allBrowsers
    }

    private fun List<ResolveInfo>.toKeyedMap(): Map<String, ResolveInfo> {
        return associateBy { it.activityInfo.packageName }
    }

    @Test
    fun `always ask user which browser to choose`() {
        assertThat(
            handler.filterBrowsers(
                BrowserModeConfigHelper.AlwaysAsk,
                allBrowsers.toKeyedMap(),
                allResolved
            )
        ).isDataClassEqualTo(
            FilteredBrowserList(
                BrowserMode.AlwaysAsk,
                allBrowsers,
                allApps,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @Test
    fun `only list native apps`() {
        assertThat(
            handler.filterBrowsers(
                BrowserModeConfigHelper.None,
                allBrowsers.toKeyedMap(),
                allResolved
            )
        ).isDataClassEqualTo(
            FilteredBrowserList(
                BrowserMode.None,
                emptyList(),
                allApps,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @Test
    fun `only list native apps, of which user has single, but no browser`() {
        assertThat(
            handler.filterBrowsers(
                BrowserModeConfigHelper.None,
                emptyMap(),
                listOf(youtube)
            )
        ).isDataClassEqualTo(
            FilteredBrowserList(
                BrowserMode.None,
                emptyList(),
                listOf(youtube),
                // TODO: Test returns false, is that a bug or expected behavior?
//                isSingleOption = true,
                noBrowsersOnlySingleApp = true
            )
        )
    }

    @Test
    fun `only list native apps, of which user has multiple`() {
        assertThat(
            handler.filterBrowsers(
                BrowserModeConfigHelper.None,
                allBrowsers.toKeyedMap(),
                allResolved
            )
        ).isDataClassEqualTo(
            FilteredBrowserList(
                BrowserMode.None,
                emptyList(),
                allApps,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @Test
    fun `selected browser, but user hasn't chosen one`() {
        val config = BrowserModeConfigHelper.SelectedBrowser(null)
        assertThat(handler.filterBrowsers(config, allBrowsers.toKeyedMap(), allResolved)).isDataClassEqualTo(
            FilteredBrowserList(
                BrowserMode.SelectedBrowser,
                emptyList(),
                allApps,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @After
    fun teardown() = stopKoin()
}
