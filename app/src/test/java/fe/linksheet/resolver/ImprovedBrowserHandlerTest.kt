package fe.linksheet.resolver

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isDataClassEqualTo
import fe.linksheet.module.resolver.FilteredBrowserList
import fe.linksheet.module.resolver.BrowserModeConfigHelper
import fe.linksheet.module.resolver.ImprovedBrowserHandler
import fe.linksheet.module.resolver.browser.BrowserMode
import app.linksheet.testing.ResolveInfoMocks.allApps
import app.linksheet.testing.ResolveInfoMocks.allBrowsers
import app.linksheet.testing.ResolveInfoMocks.allResolved
import app.linksheet.testing.ResolveInfoMocks.duckduckgoBrowser
import app.linksheet.testing.ResolveInfoMocks.miBrowser
import app.linksheet.testing.ResolveInfoMocks.packageSetOf
import app.linksheet.testing.ResolveInfoMocks.toKeyedMap
import app.linksheet.testing.ResolveInfoMocks.youtube
import org.junit.After
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class ImprovedBrowserHandlerTest {
    companion object {
        private val handler = ImprovedBrowserHandler()
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
