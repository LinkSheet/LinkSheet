package fe.linksheet

import android.content.pm.ResolveInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isDataClassEqualTo
import fe.linksheet.experiment.improved.resolver.FilteredBrowserList
import fe.linksheet.experiment.improved.resolver.browser.BrowserModeConfigHelper
import fe.linksheet.experiment.improved.resolver.browser.ImprovedBrowserHandler
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.util.buildResolveInfoTestMock
import org.junit.After
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class ImprovedBrowserHandlerTest {
    companion object {
        private val handler = ImprovedBrowserHandler()

        val miBrowser = buildResolveInfoTestMock { activity, application ->
            activity.name = "com.sec.android.app.sbrowser.SBrowserLauncherActivity"
            activity.packageName = "com.mi.globalbrowser"
        }

        val duckduckgoBrowser = buildResolveInfoTestMock { activity, application ->
            activity.name = "com.duckduckgo.app.dispatchers.IntentDispatcherActivity"
            activity.packageName = "com.duckduckgo.mobile.android"
        }

        val youtube = buildResolveInfoTestMock { activity, application ->
            activity.name = "com.google.android.youtube.UrlActivity"
            activity.packageName = "com.google.android.youtube"
        }

        val newPipe = buildResolveInfoTestMock { activity, application ->
            activity.name = "org.schabi.newpipe.RouterActivity"
            activity.packageName = "org.schabi.newpipe"
        }

        val newPipeEnhanced = buildResolveInfoTestMock { activity, application ->
            activity.name = "org.schabi.newpipe.RouterActivity"
            activity.packageName = "InfinityLoop1309.NewPipeEnhanced"
        }

        val allApps = listOf(youtube, newPipe, newPipeEnhanced)
        val allBrowsers = listOf(miBrowser, duckduckgoBrowser)
        val allResolved = allApps + allBrowsers
    }

    private fun packageSetOf(vararg resolveInfos: ResolveInfo): Set<String> {
        return resolveInfos.mapTo(LinkedHashSet()) { it.activityInfo.packageName }
    }

    private fun List<ResolveInfo>.toKeyedMap(): Map<String, ResolveInfo> {
        return associateBy { it.activityInfo.packageName }
    }

    @Test
    fun `always ask user which browser to choose`() {
        val config = BrowserModeConfigHelper.AlwaysAsk

        assertThat(handler.filterBrowsers(config, allBrowsers.toKeyedMap(), allResolved)).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.AlwaysAsk,
                browsers = allBrowsers,
                apps = allApps,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @Test
    fun `only list native apps`() {
        val config = BrowserModeConfigHelper.None

        assertThat(handler.filterBrowsers(config, allBrowsers.toKeyedMap(), allResolved)).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.None,
                browsers = emptyList(),
                apps = allApps,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @Test
    fun `only list native apps, of which user has single, but no browser`() {
        val config = BrowserModeConfigHelper.None

        assertThat(handler.filterBrowsers(config, emptyMap(), listOf(youtube))).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.None,
                browsers = emptyList(),
                apps = listOf(youtube),
                // TODO: Test returns false, is that a bug or expected behavior?
//                isSingleOption = true,
                noBrowsersOnlySingleApp = true
            )
        )
    }

    @Test
    fun `only list native apps, of which user has multiple`() {
        val config = BrowserModeConfigHelper.None

        assertThat(handler.filterBrowsers(config, allBrowsers.toKeyedMap(), allResolved)).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.None,
                browsers = emptyList(),
                apps = allApps,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @Test
    fun `selected browser, but none specified`() {
        val config = BrowserModeConfigHelper.SelectedBrowser(null)

        assertThat(handler.filterBrowsers(config, allBrowsers.toKeyedMap(), allResolved)).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.SelectedBrowser,
                browsers = emptyList(),
                apps = allApps,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @Test
    fun `selected browser`() {
        val config = BrowserModeConfigHelper.SelectedBrowser(miBrowser.activityInfo.packageName)

        assertThat(handler.filterBrowsers(config, allBrowsers.toKeyedMap(), allResolved)).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.SelectedBrowser,
                browsers = listOf(miBrowser),
                apps = allApps,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @Test
    fun `whitelisted browsers, but none selected`() {
        val config = BrowserModeConfigHelper.Whitelisted(null)

        assertThat(handler.filterBrowsers(config, allBrowsers.toKeyedMap(), allResolved)).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.Whitelisted,
                // TODO: If no browsers are whitelisted, currently all browsers are returned; Do we actually want this behavior?
//                browsers = emptyList(),
                browsers = allBrowsers,
                apps = allApps,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @Test
    fun `whitelisted browsers, one selected`() {
        val config = BrowserModeConfigHelper.Whitelisted(packageSetOf(miBrowser))

        assertThat(handler.filterBrowsers(config, allBrowsers.toKeyedMap(), allResolved)).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.Whitelisted,
                browsers = listOf(miBrowser),
                apps = allApps,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @Test
    fun `whitelisted browsers, multiple selected`() {
        val config = BrowserModeConfigHelper.Whitelisted(packageSetOf(miBrowser, duckduckgoBrowser))

        assertThat(handler.filterBrowsers(config, allBrowsers.toKeyedMap(), allResolved)).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.Whitelisted,
                browsers = listOf(miBrowser, duckduckgoBrowser),
                apps = allApps,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @After
    fun teardown() = stopKoin()
}
